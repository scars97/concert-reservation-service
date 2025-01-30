# 콘서트 예약 서비스

---

## Milestone
**12/28 ~ 1/3**
- 시나리오 선정
- 프로젝트 milestone 작성
- 다이어그램 작성
- ERD 설계
- API Specs 작성
- Mock API 작성

**1/4 ~ 1/10**
- 유저 토큰 발급 API
- 대기열 상태 확인 기능
- 예약 가능 날짜/좌석 조회 API
- 좌석 예약 API
- 잔액 충전/조회 API
- 결제 API

**1/11 ~ 1/17**
- 각 도메인 예외 처리
- 시나리오 별 동시성 테스트

**1/18 ~ 1/24**
- 시나리오 동시성 제어
- 좌석 예약 분산 락 적용


## 프로젝트 요구 사항
- 대기열 토큰 발급
- 예약 가능 날짜 / 좌석 조회
- 좌석 예약
- 잔액 충전 / 조회
- 결제

## 시퀀스 다이어그램

<details>
<summary><b>유저 대기열 토큰 발급</b></summary>

![img.png](docs/img/sequencediagram/대기열_토큰_5.png)
</details>

<details>
<summary><b>예약 가능 날짜/좌석 조회</b></summary>

![img.png](docs/img/sequencediagram/예약날짜_좌석_1.png)
</details>

<details>
<summary><b>좌석 예약</b></summary>

![img.png](docs/img/sequencediagram/좌석_예약_5.png)
</details>

<details>
<summary><b>잔액 충전 / 조회</b></summary>

![img.png](docs/img/sequencediagram/잔액_조회_충전_1.png)
</details>

<details>
<summary><b>결제</b></summary>

![img.png](docs/img/sequencediagram/결제_4.png)
</details>

## ERD
![img.png](docs/img/erd/ERD_6.png)

## API Specs

http://localhost:8080/swagger-ui/index.html

![img.png](docs/img/swagger.png)

<details>
<summary><b>콘서트 목록/상세 조회</b></summary>

### `GET /concerts`

### **Endpoint**
```http request
GET /concerts
HOST: localhost:8080
```
### **200 OK**
```json
[
  {
    "id": 1,
    "title": "A 콘서트",
    "startDate": "2024-12-14",
    "endDate": "2025-03-01"
  }
]
```

### `GET /concerts/:concertId`

### **Endpoint**
```http request
GET /concerts/1
HOST: localhost:8080
```

### **200 OK**
```json
{
  "id": 1,
  "title": "A 콘서트",
  "startDate": "2024-12-14",
  "endDate": "2025-03-01"
}
```
### **404 Not Found**
```json
GET /concerts/99
HOST: localhost:8080
{
  "status": "NOT_FOUND",
  "message": "등록되지 않은 콘서트입니다."
}
```
</details>

<details>
<summary><b>대기열 토큰 발급</b></summary>

### `POST /queues`

### **Endpoint**
```http request
POST /queues
Content-Type: application/json
```

### **Request**
```json
{
  "userId": "user1234"
}
```

### **201 Created**
**대기 상태**
```json
{
  "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
  "userId": "user1234",
  "priority": 10,
  "status": "WAIT",
  "tokenIssuedAt": "2025-01-30T11:50:000Z",
  "activeAt": null,
  "expireAt": null
}
```
### **409 Conflict**
토큰을 중복 발급하는 경우
```json
{
  "status": "CONFLICT",
  "message": "이미 토큰이 존재합니다."
}
```
</details>

<details>
<summary><b>대기열 상태 조회</b></summary>

### `GET /queues/:userId`

### **Endpoint**
```http request
GET /queues/test1234
Authorization: Bearer {tokenId}
```
### **200 OK**
대기 상태
```json
{
  "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
  "userId": "user1234",
  "priority": 10,
  "status": "WAIT",
  "tokenIssuedAt": "2025-01-30T11:50:000Z",
  "activeAt": null,
  "expireAt": null
}
```
활성화 상태 - 사용자 순서 도달 시 토큰 상태 활성화 및 만료 시간을 5분으로 설정
```json
{
  "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
  "userId": "user1234",
  "priority": 0,
  "status": "ACTIVE",
  "tokenIssuedAt": "2025-01-30T11:50:000Z",
  "activeAt": "2025-01-30T11:58:000Z",
  "expireAt": "2025-01-30T12:03:000Z"
}
```
</details>

<details>
<summary><b>예약 날짜 조회</b></summary>


### `GET /concerts/:concertId/schedules`

### **Endpoint**
```http request
GET /concerts/1/schedules
HOST: localhost:8080
Authorization: Bearer {tokenId}
```

### **200 OK**
```json
{
  "concertId": 1,
  "schedules": [
    {
      "scheduleId": 1,
      "date": "2025-03-01"
    }
  ]
}
```
</details>

<details>
<summary><b>좌석 조회</b></summary>

### `GET /concerts/schedules/:scheduleId/seats`

### **Endpoint**
```http request
GET /concerts/schedules/1/seats
HOST: localhost:8080
Authorization: Bearer {tokenId}
```

### **200 OK**
```json
{
  "scheduleId": 1,
  "seats": [
    {
      "seatId": 1,
      "seatNumber": "A1",
      "price": 75000
    },
    {
      "seatId": 2,
      "seatNumber": "B1",
      "price": 60000
    }
  ]
}
```
</details>

<details>
<summary><b>좌석 예약</b></summary>

### `POST /reservations`

### **Endpoint**
```http request
POST /reservations
Content-Type: application/json
Authorization: Bearer {tokenId}
```

### **Request**
```json
{
  "userId": "user1234",
  "concertId": 1,
  "scheduleId": 1,
  "seatId": 1
}
```
### **201 Created**

예약 성공 시 좌석 임시 배정 시간을 5분으로 설정
```json
{
  "reserveId": 1,
  "schedule": "2025-03-01",
  "seatNumber": "A1",
  "concert": {
    "concertId": 1,
    "title": "A 콘서트",
    "startDate": "2024-12-14",
    "endDate": "2025-03-01"
  },
  "price": 75000,
  "status": "TEMP",
  "createdAt": "2025-01-30T12:00:00.000Z",
  "expiredAt": "2025-01-30T12:05:00.000Z"
}
```
### **409 Conflict**
예약이 완료된 좌석을 예약하는 경우
```json
{
  "status": "CONFLICT",
  "message": "이미 예약된 좌석입니다."
}
```
</details>

<details>
<summary><b>잔액 충전 및 조회</b></summary>


### 충전 - `PATCH /points`

### **Endpoint**
```http request
PATCH /points
Content-Type: application/json
```

### **Request**
```json
{
  "userId": "user1234",
  "amount": 50000
}
```

### **200 OK**
```json
{
  "userId": "user1234",
  "point": 50000
}
```

### 조회 - `GET /points/:userId`

### **Endpoint**
```http request
GET /points/test1234
```

### **200 OK**
```json
{
  "userId": "user1234",
  "point": 50000
}
```
</details>

<details>
<summary><b>결제</b></summary>


### `POST /payments`

### **Endpoint**
```http request
POST /paytments
Content-Type: application/json
Authorization: Bearer {tokenId}
```

### **Request**
```json
{
  "userId": "user1234",
  "reserveId": 1,
  "amount": 75000
}
```

### **201 Created**
```json
{
  "paymentId": 1,
  "reserveId": 1,
  "userId": "user1234",
  "price": 75000,
  "status": "SUCCESS",
  "createdAt": "2025-01-30T12:08:00.000Z"
}
```
### **400 Bad Request**
잘못된 금액으로 요청한 경우
```json
{
  "status": "BAD_REQUEST",
  "message": "결제 금액이 일치하지 않습니다."
}
```
### **402 Payment Required**
사용자 보유 포인트가 부족한 경우
```json
{
  "status": "PAYMENT_REQUIRED",
  "message": "잔액이 부족합니다."
}
```
### **409 Conflict**
COMPLETE, CANCEL 상태의 예약건을 결제하는 경우
```json
{
  "status": "CONFLICT",
  "message": "결제할 수 없는 예약 내역입니다."
}
```
### **410 Gone**
좌석 임시 배정 시간이 만료된 예약건을 결제하는 경우
```json
{
  "status": "GONE",
  "message": "임시 예약 시간이 만료되었습니다."
}
```
</details>

## 시나리오 동시성 제어

https://recod-memory.tistory.com/110
