package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;

    // 좌석 임시 예약
    public ReservationResult tempReserve(ReservationRequest request) {
        return ReservationResult.from(reservationService.creatTempReserve(request.toInfo()));
    }

}
