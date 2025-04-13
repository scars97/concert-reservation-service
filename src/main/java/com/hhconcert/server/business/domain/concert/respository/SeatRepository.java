package com.hhconcert.server.business.domain.concert.respository;

import com.hhconcert.server.business.domain.concert.entity.Seat;

import java.util.List;

public interface SeatRepository {

    List<Seat> getSeats(Long scheduleId);

    Seat findSeat(Long seatId);
}
