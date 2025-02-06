## 대기열

---

### 기존 로직
```java
// 토큰 생성 
@Transactional
public TokenInfo createToken(String userId) {
    if (tokenRepository.getRank(userId) != null) {
        throw new BusinessException(ErrorCode.DUPLICATED_TOKEN);
    }

    Token waitToken = Token.createForWait(userId);

    int waitCount = tokenRepository.getTokenCountFor(TokenStatus.WAIT);

    return TokenInfo.from(tokenRepository.createToken(waitToken));
}

// 대기열 상태 요청
public TokenInfo checkQueueStatus(TokenInfo info) {
    int currentPriority = 0;

    // WAIT 상태인 경우, 대기 순서 연산
    if (info.status() == TokenStatus.WAIT) {
        List<Token> waitTokens = tokenRepository.getTokensBy(TokenStatus.WAIT);
        currentPriority = (int) waitTokens.stream()
                .filter(t -> t.getTokenIssuedAt().isBefore(info.tokenIssuedAt()))
                .count() + 1;
    }

    return TokenInfo.from(info, currentPriority);
}
```
현재 토큰 생성 로직에서 WAIT 상태 토큰을 생성하고, DB에서 WAIT 토큰 개수를 조회하여 대기 순서를 반환한다.

---

### 문제점

**1. 대기 순서값 불일치**

동시 요청이 발생한다면, 토큰은 정상적으로 생성되지만 대기 순서가 정확하지 않을 수 있다. <br>
대기열 상태 요청 또한, 같은 문제점을 가지고 있다.

**2. DB 부하**

좌석 조회와 더불어 동시 요청이 많은 토큰 발급 및 대기열 상태 요청이 모두 DB 를 바라본다면 부하가 클 것으로 예상된다.

레디스를 활용한다면 정확한 대기 순서값을 반환하고, DB 부하도 줄여볼 수 있을 것 같다.

그렇다면 토큰 DB를 유지할 필요가 있을까?

토큰은 변경이 잦고 휘발성이 강한 객체다.

사용자가 대기 토큰을 발급받으면 대기 순서는 준실시간으로 변경되어야하고, 새로고침 하는 경우에도 변경되어야 한다.

토큰이 활성화되면 기존 대기 토큰 삭제, 활성화 토큰 만료 시간 설정 등 수시로 command가 일어난다.

토큰 이력을 남기기 위해 DB를 유지하는 것도 불필요하다 생각된다.

- as-is : 토큰 DB
- to-be : 토큰 DB X → 레디스 사용

레디스에서 토큰을 관리하기 위해 2개의 자료 구조를 사용하려 한다.

---

### **Sorted Set**

먼저 sorted set은 토큰 생성과 대기 순서, 토큰 만료 시간 관리를 위해 사용한다.

```java
대기 토큰 생성
-> ZADD wait-token 현재시간 "사용자ID"

대기 순서 조회
-> ZRANK wait-token "사용자ID"

대기 토큰 -> 활성화 토큰
-> ZPOPMIN wait-token 활성화가능개수 // result : "user1", "user2"
-> ZADD active-token 현재시간 "사용자ID"

토큰 만료 처리
-> ZREMRANGEBYSCORE active-token 0 현재시간
```

### **String**

String 을 사용하는 이유는 토큰 세부 정보를 저장하기 위함이다.

Sorted Set 으로만 토큰을 관리하게 되면 단일 정보밖에 저장하지 못하고,
사용자에게 응답할 정보가 부족하다고 생각된다. 

→ 사용자ID, 토큰 상태 등…

도메인 모델을 만들어서 레디스에 객체를 저장해볼 수 있지 않을까.

```java
public record TokenVO(
    String tokenId,
    String userId,
    TokenStatus status,
    LocalDateTime tokenIssuedAt,
    ...
){
}

// 레디스 - TokenVO 저장
public void addTokenForValue(TokenVO token) {
    String jsonData;
    try {
        jsonData = objectMapper.writeValueAsString(token);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Serialisation failure", e);
    }

    redisTemplate.opsForValue().set(token.userId(), jsonData);
}

// 예상 레디스 명령어
-> SET 사용자ID "{tokenId:'토큰ID',userId:'사용자ID',status:'WAIT',...}"
```

반대로 조회할 때는 역직렬화하여 TokenVO를 반환하도록 한다.

```java
// 토큰 조회
public TokenVO findTokenAtValue(String userId) {
    var tokenData = redisTemplate.opsForValue().get(userId);

    if (tokenData == null) {
        throw new NoSuchElementException("등록되지 않은 토큰입니다.");
    }

    try {
        return objectMapper.readValue(
		        tokenData.toString(), 
		        objectMapper.getTypeFactory().constructType(TokenVO.class)
        );
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Deserialisation failure", e);
    }
}
```

Sorted Set에서는 wait-token, active-token. 대기/활성 토큰에 대한 키를 나눴다.

String 에서는 사용자ID 값이 키가 되고 토큰 상태가 변경되면 그대로 덮어씌운다.

```java
대기 토큰 생성
-> SET 사용자ID "{tokenId:'토큰ID',userId:'사용자ID',status:'WAIT',...}"

대기 토큰 -> 활성화 토큰
-> SETEX 300 사용자ID "{tokenId:'토큰ID',userId:'사용자ID',status:'ACTIVE',...}"
```

한 가지 다른 것은 덮어씌움과 동시에 변경된 사용자 토큰에 TTL을 설정한다.

TTL로 인해 sorted set 처럼 만료 토큰을 따로 관리하지 않아도 된다는 장점이 있다.