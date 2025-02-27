## 1. 개요

---

- 목적 : 부하 테스트를 통해 시스템의 안정성, 성능 한계, 병목 지점을 확인한다.
- 범위 : 콘서트 예약 서비스의 핵심 기능 (대기열, 좌석 예약) 에 대한 부하 테스트 수행.

## 2. 부하 테스트 대상

---

### 2.1 대기열

- 선정 이유 :
    - 서비스 중 트래픽이 가장 몰릴 것으로 예상
    - 일시적인 부하로 인해 병목 현상 발생 가능성 ↑
- 테스트 포인트 :
    - 토큰 생성 성공/실패 → 요청 성공/실패 비율 확인
    - 토큰 생성까지 걸린 시간 확인

서비스 중 트래픽이 가장 집중될 것으로 예상되며,
사용자들이 특정 시점에 대기열에 진입할 때 모든 부하를 처리해야하기 때문에 SPOF 가능성 존재.

### 2.2 좌석 예약

- 선정 이유 :
    - 동시성 이슈 발생 가능성이 높아 요청이 실패할 수 있음.
    - 콘서트, 스케줄, 좌석 등 조회 로직이 많아 DB 부하 클 것으로 예상 됨.
    - 분산 락 처리로 인해 API 지연 가능성 높음
- 테스트 포인트 :
    - 동시성 제어로 인한 요청 실패율 확인.
    - 요청이 시작되기 전까지 차단된 시간 확인 → 락 처리로 인한 대기 발생 가능성 ↑

## 3. 테스트 시나리오

---

### 3.1 대기열

대규모 사용자가 대기열에 진입할 때 최대 처리량을 측정하고, 시스템이 안정적으로 동작하는지 확인한다.

- 테스트 흐름:
    - 사용자 대기열 진입 요청을 보냄
    - 서버에서 해당 사용자의 중복 토큰 여부 확인
    - 중복이 없으면 대기열 토큰 발급 및 저장
- 부하 조건:
    - 1000명의 사용자가 각각 1회씩 대기열 진입 요청
    - 동시에 1000명 요청 발생 (순간적인 부하 테스트)

### 3.2 좌석 예약

다수의 사용자가 동일한 좌석을 예약 요청할 떄, 예약 성공률과 실패율을 확인하고, 경합 조건이 발생하는지 분석한다.

- 테스트 흐름:
    - 사용자의 정보, 콘서트, 일정, 좌석 정보 조회
    - 해당 좌석이 이미 예약되었는지 확인
    - 첫 번째 성공 요청만 예약 확정, 이후 들어오는 요청은 실패 처리
- 부하 조건:
    - 1000명의 사용자가 동일한 좌석에 동시에 예약 요청
    - 순간적으로 1000건의 요청 발생

## 4. 테스트 실행

---

### 4.1 대기열

**(1) 테스트 스크릡트**
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        gradual_uinque_users: {
            executor: 'per-vu-iterations',
            vus: 1000,            // 1000명의 가상 사용자
            iterations: 1,        // 각 사용자 1회 요청
            maxDuration: '5m',    // 최대 실행 시간
            startTime: '0s',
        },
    },
  };

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    const userId = 'user' + __VU; // 사용자 ID 범위: 1 ~ 1000

    // 토큰 발급
    let tokenResponse = http.post(`${BASE_URL}/queues`, 
        JSON.stringify({ userId: userId }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(tokenResponse, { '토큰 생성' : (r) => r.status === 201 });

    sleep(1);
}
```

**(2) 테스트 결과**
```
checks.........................: 100.00% 1000 out of 1000
data_received..................: 315 kB  84 kB/s
data_sent......................: 170 kB  45 kB/s
http_req_blocked...............: avg=145.87ms min=7.16ms   med=131.31ms max=361.33ms p(90)=267.22ms p(95)=271.85ms
http_req_connecting............: avg=88.33ms  min=7.1ms    med=71.51ms  max=253.67ms p(90)=146.57ms p(95)=171.1ms
http_req_duration..............: avg=1.28s    min=103.4ms  med=1.33s    max=2.3s     p(90)=2.06s    p(95)=2.16s
 { expected_response:true }...: avg=1.28s    min=103.4ms  med=1.33s    max=2.3s     p(90)=2.06s    p(95)=2.16s
http_req_failed................: 0.00%   0 out of 1000
http_req_receiving.............: avg=261.45µs min=17.76µs  med=130.61µs max=12.99ms  p(90)=662.93µs p(95)=791.1µs
http_req_sending...............: avg=336.6µs  min=11.24µs  med=74.25µs  max=4.04ms   p(90)=985.12µs p(95)=1.42ms
http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
http_req_waiting...............: avg=1.28s    min=103.19ms med=1.32s    max=2.3s     p(90)=2.06s    p(95)=2.16s
http_reqs......................: 1000    265.157479/s
iteration_duration.............: avg=2.43s    min=1.19s    med=2.5s     max=3.45s    p(90)=3.21s    p(95)=3.33s
iterations.....................: 1000    265.157479/s
vus............................: 370     min=370          max=1000
vus_max........................: 1000    min=1000         max=1000

```

**(3) 성능 지표**
- 요청 수 : 1000명 요청 중 100% 정상 처리
- 평균 응답 시간 : 1.28s
- 최소 응답 시간 : 103.4ms
- 최대 응답 시간 : 2.3s
- 90번째 백분위수 응답 시간 : 2.06s
- 95번째 백분위수 응답 시간 : 2.16s

### 4.2 좌석 예약

**(1) 테스트 스크립트**
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        gradual_uinque_users: {
            executor: 'per-vu-iterations',
            vus: 1000,            // 1000명의 가상 사용자
            iterations: 1,        // 각 사용자 1회 요청
            maxDuration: '5m',    // 최대 실행 시간
            startTime: '0s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // 95% 요청이 500ms 이하
        http_req_failed: ['rate<0.05'],   // 실패율 5% 이하
    },
};

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    const userId = 'user' + __VU;

    // 토큰 발급
    let createTokenResponse = http.post(`${BASE_URL}/queues`,
        JSON.stringify({ userId: userId }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    const tokenId = createTokenResponse.json().tokenId;
    console.log('토큰 ID : ' + tokenId);

    // 토큰 상태 확인
    let status = 'WAIT';
    let attempts = 0;
    const maxAttempts = 15;

    while (status !== 'ACTIVE' && attempts < maxAttempts) {
        sleep(3);

        let queueStatusResponse = http.get(`${BASE_URL}/queues/${userId}`,
            { headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + tokenId }}
        );

        status = queueStatusResponse.status === 200 ? queueStatusResponse.json().status : status;

        if (status === 'ACTIVE') break;

        attempts++;
    }

    // 토큰 활성화 좌석 예약
    if (status === 'ACTIVE') {
        const concertId = 1;
        const scheduleId = 1;
        const seatId = 1;

        let reservationResponse = http.post(`${BASE_URL}/reservations`,
            JSON.stringify({
                userId: userId,
                concertId: concertId,
                scheduleId: scheduleId,
                seatId: seatId
            }),
            { headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + tokenId }}
        );

        check(reservationResponse, {
            'Seat: reservation success': (r) => r.status === 201,
            'Seat: reservation conflict': (r) => r.status === 409
        });
    } else {
        console.log(`토큰이 활성화되지 않았습니다.`);
    }

    sleep(1);
}
```

**(2) 테스트 결과**
```
✗ Seat: reservation success
      ↳  0% — ✓ 1 / ✗ 999
     ✗ Seat: reservation conflict
      ↳  99% — ✓ 996 / ✗ 4

   checks.........................: 49.85% 997 out of 2000
   data_received..................: 1.2 MB 58 kB/s
   data_sent......................: 1.1 MB 54 kB/s
   http_req_blocked...............: avg=30.3ms   min=1.66µs  med=7.59µs   max=517.58ms p(90)=137.68ms p(95)=172.75ms
   http_req_connecting............: avg=21.18ms  min=0s      med=0s       max=322.42ms p(90)=99.12ms  p(95)=147.95ms
 ✗ http_req_duration..............: avg=604.1ms  min=2.11ms  med=5.26ms   max=5.02s    p(90)=2.15s    p(95)=2.59s
     { expected_response:true }...: avg=852.99ms min=3.44ms  med=78.35ms  max=3.25s    p(90)=2.59s    p(95)=2.89s
 ✗ http_req_failed................: 61.11% 3145 out of 5146
   http_req_receiving.............: avg=198.29µs min=12.14µs med=108.37µs max=21.81ms  p(90)=397.94µs p(95)=605.41µs
   http_req_sending...............: avg=90µs     min=4.96µs  med=33.58µs  max=92.42ms  p(90)=98.24µs  p(95)=195.04µs
   http_req_tls_handshaking.......: avg=0s       min=0s      med=0s       max=0s       p(90)=0s       p(95)=0s
   http_req_waiting...............: avg=603.81ms min=1.96ms  med=5.02ms   max=5.01s    p(90)=2.15s    p(95)=2.59s
   http_reqs......................: 5146   246.366503/s
   iteration_duration.............: avg=13.7s    min=4.77s   med=13.74s   max=20.55s   p(90)=19.41s   p(95)=19.98s
   iterations.....................: 1000   47.875341/s
   vus............................: 90     min=90           max=1000
   vus_max........................: 1000   min=1000         max=1000
```

**(3) 성능 지표**
- 예약 성공 : 1명 (1/999)
- 좌석 충돌 : 996명 → 이미 예약된 좌석을 요청해서 실패한 케이스
- 기타 실패 : 4명 → HTTP 요청 자체가 실패한 케이스
- 평균 응답 시간 : 604.1ms
- 최소 응답 시간 : 2.11ms
- 최대 응답 시간 : 5.02s
- 90번째 백분위수 응답 시간 : 2.15s
- 95번째 백분위수 응답 시간 : 2.59s

## 5. 성능 지표 분석 및 개선 방안

---

|  | 평균 응답 시간 | 최소 응답 시간 |  최대 응답 시간 | p(90) 응답 시간 | p(95) 응답 시간 |
| --- | --- | --- | --- | --- | --- |
| 대기열 | 1.28s | 103.4ms | 2.3s | 2.06s | 2.16s |
| 좌석 예약 | 604.1ms | 2.11ms | 5.02s | 2.15s | 2.59s |
- 대기열과 좌석 예약 모두 p90 이상에서 2초 이상의 응답 시간.
- 특히 최대 응답 시간이 2~3초로 일부 요청이 지연되는 문제 발생.
- 동시 요청 부하가 커질 때 성능 저하.

### 5.1 대기열 진입 지연

**원인**
- 대기열 진입 시, 중복 토큰 확인을 위한 조회 후 토큰 생성으로 인해 응답 속도 저하 → 네트워크 I/O 비용 증가
- 대량 쓰기 작업이 발생할 때 Redis 성능 저하 발생 가능성 ↑

**개선 방안**
- Redis Pipeline 사용
    - Redis 조회 + 저장을 Lua 스크립트 또는 pipeline을 사용하여 하나의 요청으로 처리 → 네트워크 오버헤드 ↓
- Redis 쓰기 부하 분산 & 최적화
    - Redis 클러스터를 사용하여 여러 노드로 데이터 분산 처리
    - Redis AOF 를 설정하여 디스크 부담을 줄여 쓰기 최적화

### 5.2 좌석 예약 응답 지연

**원인**
- 경합 조건 발생
    - 동시에 1000명이 하나의 좌석을 예약하는 과정에서 락 처리로 인해 대기 시간 증가.
- DB 부하
    - 좌석 예약 시 불필요한 트랜잭션 처리로 인해 DB 부하 증가
- 네트워크 및 서버 자원 문제
    - 동시 요청이 급증하면서 CPU 사용량 ↑ & 병목 현상 발생 가능성 ↑

**개선 방안**
- 불필요한 트랜잭션 처리 최소화
    - 좌석 예약을 위해 사용자, 콘서트, 스케줄, 좌석 등을 개별적으로 조회하기 때문에 성능 저하 발생 가능성 있음
    - 좌석 예약에 필요한 최소한의 정보만 조회
    - 예약 완료된 좌석 정보를 Redis 에 저장하여 DB 조회 부하 감소
- 서버 부하 분산 (로드 밸런싱 & Rate Limiting)
    - 초당 최대 요청 수 제한을 설정하여 서버 과부하 방지