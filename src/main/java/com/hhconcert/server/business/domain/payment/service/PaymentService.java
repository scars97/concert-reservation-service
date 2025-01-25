package com.hhconcert.server.business.domain.payment.service;

import com.hhconcert.server.business.domain.payment.dto.PaymentCommand;
import com.hhconcert.server.business.domain.payment.dto.PaymentInfo;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.exception.PaymentErrorCode;
import com.hhconcert.server.business.domain.payment.exception.PaymentException;
import com.hhconcert.server.business.domain.payment.persistance.PaymentRepository;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public PaymentInfo payment(PaymentCommand command) {
        Reservation reservation = reservationRepository.findReserve(command.reserveId());
        if (reservation.isNotMatchAmount(command.amount())) {
            throw new PaymentException(PaymentErrorCode.NOT_MATCH_PAYMENT_AMOUNT);
        }

        reservation.updateForComplete();

        User user = userRepository.findUser(command.userId());
        user.usePoint(command.amount());

        tokenRepository.dropTokenByUserId(command.userId());

        Payment payment = Payment.create(user, reservation, reservation.getPrice());

        return PaymentInfo.from(paymentRepository.payment(payment));
    }

}
