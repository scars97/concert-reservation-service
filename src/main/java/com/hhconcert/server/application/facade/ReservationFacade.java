package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.application.event.reservation.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.global.common.lock.DistributedLock;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ApplicationEventPublisher eventPublisher;

    @DistributedLock(key = "'reserve:'.concat(#request.seatId())")
    @Transactional
    public ReservationResult tempReserve(ReservationRequest request) {
        ReservationInfo info = reservationService.creatTempReserve(request.toInfo());

        eventPublisher.publishEvent(ReserveSuccessEvent.of(info));

        return ReservationResult.from(info);
    }

}
