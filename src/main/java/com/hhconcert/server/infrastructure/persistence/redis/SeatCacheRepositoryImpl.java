package com.hhconcert.server.infrastructure.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.business.concert.dto.SeatInfo;
import com.hhconcert.server.business.concert.respository.SeatCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SeatCacheRepositoryImpl implements SeatCacheRepository {

    private static final String AVAILABLE_SEATS_PREFIX = "available-seats:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveAvailableSeats(Long scheduleId, List<SeatInfo> seats) {
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(seats);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialisation failure", e);
        }

        redisTemplate.opsForValue().set(AVAILABLE_SEATS_PREFIX + scheduleId, jsonData);
    }

    @Override
    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        var seats = redisTemplate.opsForValue().get(AVAILABLE_SEATS_PREFIX + scheduleId);

        if (seats == null) return null;

        try {
            return objectMapper.readValue(seats.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, SeatInfo.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialisation failure", e);
        }
    }

    @Override
    public void evictCacheBy(Long scheduleId) {
        redisTemplate.delete(AVAILABLE_SEATS_PREFIX + scheduleId);
    }

    @Override
    public void evictAllCache() {
        Set<String> keys = redisTemplate.keys(AVAILABLE_SEATS_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
