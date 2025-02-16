package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.application.event.ReservationEventPublisher;
import com.hhconcert.server.application.event.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ReservationEventPublisher reservationEventPublisher;

    @Transactional
    public ReservationResult tempReserve(ReservationRequest request) {
        ReservationInfo info = reservationService.creatTempReserve(request.toInfo());

        reservationEventPublisher.publish(ReserveSuccessEvent.of(info.reserveId()));

        return ReservationResult.from(info);
    }

}
