package com.hhconcert.server.business.domain.seat.persistance;

import com.hhconcert.server.business.domain.seat.entity.Seat;

import java.util.List;

public interface SeatRepository {

    List<Seat> getSeats(Long scheduleId);

    Seat findSeat(Long seatId);
}
