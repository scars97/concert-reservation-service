package com.hhconcert.server.business.domain.payment.service;

import com.hhconcert.server.business.domain.payment.dto.PaymentInfo;
import com.hhconcert.server.business.domain.payment.dto.PaymentResult;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.persistance.PaymentRepository;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import com.hhconcert.server.business.domain.payment.exception.PaymentErrorCode;
import com.hhconcert.server.business.domain.payment.exception.PaymentException;
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
    public PaymentResult payment(PaymentInfo info) {
        Reservation reservation = reservationRepository.findReserve(info.reserveId());
        if (reservation.isNotMatchAmount(info.amount())) {
            throw new PaymentException(PaymentErrorCode.NOT_MATCH_PAYMENT_AMOUNT);
        }

        User user = userRepository.findUser(info.userId());
        user.usePoint(info.amount());

        reservation.updateForComplete();

        tokenRepository.dropTokenByUserId(info.userId());

        Payment payment = Payment.create(user, reservation, reservation.getPrice());

        return PaymentResult.from(paymentRepository.payment(payment));
    }

}
