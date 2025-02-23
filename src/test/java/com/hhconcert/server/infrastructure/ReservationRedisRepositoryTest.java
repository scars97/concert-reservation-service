package com.hhconcert.server.infrastructure;

import com.hhconcert.server.infrastructure.reservation.ReservationRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ReservationRedisRepositoryTest {

    @Autowired
    private ReservationRedisRepository redisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.delete("reserved-seats");
    }

    @DisplayName("좌석 번호가 redis 에 저장된다.")
    @Test
    void addReservedSeat() {
        Long reservedSeatId = 1L;
        Long currentTime = System.currentTimeMillis();

        redisRepository.zSetAdd(reservedSeatId, currentTime);

        Set<Long> results = redisRepository.zSetGet();
        assertThat(results).hasSize(1).contains(reservedSeatId);
    }

    @DisplayName("만료 시간이 지난 좌석 ID가 조회된다.")
    @Test
    void getExpireReservedSeats() {
        Long currentTime = System.currentTimeMillis();
        Long expiryTime1 = currentTime - 1L;
        Long expiryTime2 = currentTime - 2L;
        Long notExpiryTime = currentTime + 1;

        redisRepository.zSetAdd(1L, expiryTime1);
        redisRepository.zSetAdd(2L, expiryTime2);
        redisRepository.zSetAdd(3L, notExpiryTime);

        Set<Long> results = redisRepository.zSetGetByScore(currentTime);
        assertThat(results).hasSize(2)
                .contains(1L, 2L);
    }

    @DisplayName("만료 시간이 지난 좌석 ID가 삭제된다.")
    @Test
    void dropExpireReservedSeat() {
        Long currentTime = System.currentTimeMillis();
        Long expiryTime1 = currentTime - 1;
        Long expiryTime2 = currentTime - 2;
        Long notExpiryTime = currentTime + 1;

        redisRepository.zSetAdd(1L, expiryTime1);
        redisRepository.zSetAdd(2L, expiryTime2);
        redisRepository.zSetAdd(3L, notExpiryTime);

        Long result = redisRepository.zSetRemoveByScore(currentTime);
        assertThat(result).isEqualTo(2);
    }

}