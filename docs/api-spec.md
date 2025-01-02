## **콘서트 목록/상세 조회**

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
    "title": "콘서트명1",
    "startDate": "2024-12-31",
    "endDate": "2025-01-01"
  },
  {
    "id": 2,
    "title": "콘서트명2",
    "startDate": "2025-01-01",
    "endDate": "2025-01-02"
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
  "title": "콘서트명1",
  "startDate": "2024-12-31",
  "endDate": "2025-01-01"
  
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

---

## **대기열 토큰 발급**

### `POST /queues/tokens`

### **Endpoint**
```http request
POST /queues/tokens
Content-Type: application/json
```

### **Request**
```json
{
  "userId": "test1234",
  "concertId": 1
}
```

### **201 Created**
```json
{
  "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
  "userId": "test1234",
  "concertId": 1,
  "priority": 34,
  "status": "WAIT"
}
```

## 대기열 상태 조회

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
  "userId": "test1234",
  "concertId": 1,
  "priority": 1,
  "status": "WAIT"
}
```
사용자 순서 도달 시 토큰 상태 활성화 및 만료 시간을 5분으로 설정
```json
{
  "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
  "userId": "test1234",
  "concertId": 1,
  "priority": 0,
  "status": "ACTIVE"
}
```
### **401 Unauthorized**
Authorization 정보가 없는 경우
```json
{
  "status": "UNAUTHORIZED",
  "message": "토큰 정보가 누락되었습니다."
}
```
### **403 Forbidden**
잘못된 토큰인 경우
```json
{
  "status": "FORBIDDEN",
  "message": "유효하지 않은 토큰입니다."
}
```

---

## **예약 날짜 조회**

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
      "date": "2024-12-31"
    },
    {
      "scheduleId": 2,
      "date": "2025-01-01"
    }
  ]
}
```

---

## 좌석 조회

### `GET /schedules/:scheduleId/seats`

### **Endpoint**
```http request
GET /schedules/1/seats HTTP/1.1
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
      "price": 75000,
      "available": "Y"
    },
    {
      "seatId": 2,
      "seatNumber": "B1",
      "price": 60000,
      "available": "N"
    }
  ]
}
```

---

## 좌석 예약

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
  "userId": "test1234",
  "concertId": 1,
  "scheduleId": 1,
  "seatId": 1
}
```
**201 Created**

좌석 예약 성공 시 대기열 토큰 삭제 -> 좌석 임시 배정 시간을 5분으로 설정
```json
{
  "reserveId": 1,
  "schedule": "2025-01-01",
  "seatNumber": "A1",
  "concert": {
    "concertId": 1,
    "title": "콘서트명1",
    "startAt": "2024-12-31",
    "endAt": "2025-01-01"
  },
  "price": 75000,
  "status": "TEMP",
  "reservedAt": "2024-12-30T00:05:33Z",
  "expiredAt": "2024-12-30T00:10:33Z"
}
```

---

## 잔액 충전 및 조회

### 충전 - `PATCH /points`

### **Endpoint**
```http request
PATCH /points
Content-Type: application/json
```

### **Request**
```json
{
  "userId": "test1234",
  "amount": 100000
}
```

### 200 OK
```json
{
  "userId": "test1234",
  "point": 100000
}
```

### 조회 - `GET /points/:userId`

### **Endpoint**
```http request
GET /points/test1234
```

### 200 OK
```json
{
  "userId": "test1234",
  "point": 170000
}
```

---

## 결제

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
  "userId": "test1234",
  "reserveId": 1,
  "amount": 75000
}
```

### **201 Created**
```json
{
  "payment_id": 1,
  "reserveId": 1,
  "user_id": "test1234",
  "price": 75000,
  "status": "COMPLETE",
  "created_at": "2024-12-30T00:13:33Z"
}
```
### **402 Payment Required**
```json
{
  "status": "PAYMENT_REQUIRED",
  "message": "포인트가 부족합니다."
}
```