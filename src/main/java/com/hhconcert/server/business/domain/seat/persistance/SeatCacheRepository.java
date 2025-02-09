package com.hhconcert.server.business.domain.seat.persistance;

import com.hhconcert.server.business.domain.seat.dto.SeatInfo;

import java.util.List;

public interface SeatCacheRepository {

    void saveAvailableSeats(Long scheduleId, List<SeatInfo> seats);

    List<SeatInfo> getAvailableSeats(Long scheduleId);

    void evictCacheBy(Long scheduleId);

    void evictAllCache();

}
