package com.hhconcert.server.infrastructure.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationRedisRepository {

    private static final String RESERVED_SEAT_KEY = "reserved-seats";
    private final RedisTemplate<String, Object> redisTemplate;

    public void zSetAdd(Long seatId, Long currentTime) {
        redisTemplate.opsForZSet().add(RESERVED_SEAT_KEY, seatId, currentTime);
    }

    public Set<Long> zSetGet() {
        Set<Object> seats = redisTemplate.opsForZSet().range(RESERVED_SEAT_KEY, 0, -1);
        return seats.stream()
                .map(o -> Long.valueOf(o.toString()))
                .collect(Collectors.toSet());
    }

    public Set<Long> zSetGetByScore(Long currentTime) {
        Set<Object> expiredSeats = redisTemplate.opsForZSet().rangeByScore(RESERVED_SEAT_KEY, 0, currentTime);
        return expiredSeats.stream()
                .map(o -> Long.valueOf(o.toString()))
                .collect(Collectors.toSet());
    }

    public Long zSetRemoveByScore(Long currentTime) {
        return redisTemplate.opsForZSet().removeRangeByScore(RESERVED_SEAT_KEY, 0, currentTime);
    }

}
