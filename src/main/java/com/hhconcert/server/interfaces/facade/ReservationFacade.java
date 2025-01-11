package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;

    // 좌석 임시 예약
    public ReservationResponse tempReserve(ReservationRequest request) {
        return ReservationResponse.from(reservationService.creatTempReserve(request.toInfo()));
    }

}
