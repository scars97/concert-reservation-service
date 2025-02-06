## **좌석 조회 & 예약 개선**

---

### 기존 로직
```java
// 좌석 조회 - 전체 좌석 조회 후, 예약 내역이 있는 좌석을 필터링
public List<SeatInfo> getAvailableSeats(Long scheduleId) {
    List<Seat> seats = seatRepository.getSeats(scheduleId);

    return seats.stream()
            .filter(s -> reservationRepository.getSeatReserve(s.getId()).isEmpty())
            .map(SeatInfo::from)
            .toList();
}

// 예약 생성 - 예약된 좌석인지 확인하고 임시 예약 내역 생성.
public ReservationInfo creatTempReserve(ReservationCommand info) {
    User user = userRepository.findUser(info.userId());
    Concert concert = concertRepository.findConcert(info.concertId());
    Schedule schedule = scheduleRepository.findSchedule(info.scheduleId());
    Seat seat = seatRepository.findSeat(info.seatId());

    reservationRepository.getSeatReserve(seat.getId())
            .ifPresent(reservation -> {
                throw new BusinessException(ErrorCode.ALREADY_RESERVED);
            });

    Reservation reservation = Reservation.createTemp(user, concert, schedule, seat);

    return ReservationInfo.from(reservationRepository.createTempReserve(reservation));
}
```

좌석 상태를 관리하지 않아 좌석 조회, 예약 모두 해당 좌석에 대한 예약 내역이 있는지 조회한다.

읽기 작업이 많은 좌석 조회의 경우, 매번 예약 DB를 봐야하기 때문에 비용이 클 것으로 예상된다.

좌석 예약 내역 조회를 DB가 아닌 레디스로 이관하여 성능을 높이고자 한다.

---

### 예약된 좌석 정보 Redis 저장

레디스에 예약된 좌석 정보를 넣어주기 위해 예약 로직에 레디스 관련 코드를 추가해줘야 한다.

```java
// 예약 생성
public ReservationInfo creatTempReserve(ReservationCommand info) {
    // ...
    
    // 레디스 - 예약된 좌석 ID 등록
    // SADD reserved-seats "좌석ID"
    reservationCacheStorage.addReservedSeat(seat.getId());

		// ...
}
```
예약이 생성될 때마다 레디스에 예약된 좌석 정보가 등록되도록 한다.

---

### 예약 만료 스케줄러 추가
예약이 생성되면 해당 좌석에 대해 임시 배정 시간이 부여되는데,
임시 배정 시간이 만료되는 경우, 레디스에서 만료된 좌석 ID를 삭제해줘야 한다.

예약된 좌석마다 만료 시간이 다르기 때문에 만료 스케줄러를 구현하여 관리해줘야 한다.

이전에 Set 구조를 사용해서 예약 좌석ID를 저장하도록 했는데, 좌석 ID 만으로 만료 시간이 지났는지 알 수 없다.

Sorted Set 으로 변경하여 score - 만료 시간, value - 좌석 ID 를 저장하는 방식을 사용하려 한다.

```java
AS-IS
-> SADD reserved-seats "좌석ID"

TO-BE
-> ZADD reserved-seats 만료시간 "좌석ID"
```

```java
// 좌석 예약 만료 스케줄러
@Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
public void expireReserveSeat() {

    // 레디스 - 만료된 좌석ID 조회
    // zrangebyscore reserved-seats 0 현재시간
    Set<Long> expireSeats = reservationCacheStorage.getExpireSeatReserves();
    
    // DB - 만료된 예약 상태 변경 TEMP -> CANCEL
    for (seatId : expireSeats) {
		    reservationRepository.cancel(seatId);
    }
    
    // 레디스 - 만료된 좌석 ID 삭제
    // zremrangebyscore reserved-seat 0 현재시간
    reservationCacheStorage.dropSeatReservesBy(현재시간);
}
```

만료된 좌석 삭제, 예약 상태 변경을 주기적으로 실행하여 사용자가 좌석 목록 조회 시, 최신화된 데이터를 반환하도록 한다.

DB에서 레디스로 이관하면서 좌석 조회 DB 부하를 줄일 수 있을 것으로 예상된다.

좌석 조회에서는 기존 예약 DB 조회를 레디스 조회로 변경한다.

```java
public List<SeatInfo> getAvailableSeats(Long scheduleId) {
    List<Seat> seats = seatRepository.getSeats(scheduleId);
    
    // 레디스에서 예약된 좌석 ID 조회
    Set<Long> reservedSeat = reservationCacheStorage.getSeatReserves();

    // 예약되지 않은 좌석만 필터링
    return seats.stream()
            .filter(seat -> !reservedSeat.contains(seat.getId()))
            .map(SeatInfo::from)
            .toList();
}
```

---

### 좌석 조회 캐싱
기존 DB 부하를 그대로 레디스가 받기 때문에 여전히 개선이 필요하다.

getAvailableSeats에서 반환되는 값을 캐시할 수 있지 않을까.

준실시간으로 조회되는 좌석 데이터를 캐시하면 부하가 크게 줄어들 것으로 예상된다.

하지만 예약 생성, 만료 스케줄러로 인해 데이터 정합성 문제가 발생할 수 있다.

그래서?

- 예약 생성과 만료 스케줄러가 실행될 때마다 getAvailableSeats 캐시 Evict
- 좌석 목록은 캐시 데이터만 사용. → Evict 된 경우, 로직 실행

```java
@Cacheable(value = "available-seats", key = "#scheduleId")
public List<SeatInfo> getAvailableSeats(Long scheduleId) {
		// 좌석 정보 조회 = DB 조회 ( seat 에는 상태 x )
    List<Seat> seats = seatRepository.getSeats(scheduleId);
		
		// 레디스에서 예약된 좌석 ID 조회
    Set<Long> reservedSeat = reservationCacheStorage.getSeatReserves();

    // 예약 가능한 좌석만 필터링
    return seats.stream()
            .filter(seat -> !reservedSeat.contains(seat.getId()))
            .map(SeatInfo::from)
            .toList();
}

// 예약 생성
@CacheEvict(value = "available-seats", key = "#scheduleId")
@Transactional
public ReservationInfo creatTempReserve(ReservationCommand info) {
    // ...
}

// 만료 스케줄러
@CacheEvict(value = "available-seats", allEntries = true)
@Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
@Transactional
public void expireReserveSeat() {
		// ...
}
```

좌석 목록 조회 시 캐시가 없으면 로직 실행 후 반환값을 캐시에 저장한다.

만약 캐시가 없는 상태에서 동시 요청이 발생한다면, 요청마다 로직을 실행하게 되어 서버 부하가 발생하는 캐시 스탬피드 현상이 일어난다.



예약 생성과 만료 스케줄러에서 Evict 대신 캐시를 업데이트 해주는 방식으로 구현한다면

available-seats 캐시는 없어지지 않고 항상 최신 데이터로 유지 되어 캐시 스탬피드 문제를 방지할 수 있다.