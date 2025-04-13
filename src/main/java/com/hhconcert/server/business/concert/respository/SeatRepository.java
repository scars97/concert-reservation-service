package com.hhconcert.server.business.concert.respository;

import com.hhconcert.server.business.concert.domain.Seat;

import java.util.List;

public interface SeatRepository {

    List<Seat> getSeats(Long scheduleId);

    Seat findSeat(Long seatId);
}
