package com.hhconcert.server.business.domain.user;

import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.global.common.error.ErrorCode;
import com.hhconcert.server.global.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    User user;

    @BeforeEach
    void setUp() {
        user = new User("test1234", 60000);
    }

    @DisplayName("포인트가 충전된다.")
    @Test
    void chargePoint() {
        int amount = 15000;

        user.chargePoint(amount);

        assertThat(user.getPoint()).isEqualTo(75000);
    }

    @DisplayName("포인트가 사용된다.")
    @Test
    void usePoint() {
        int amount = 50000;

        user.usePoint(amount);

        assertThat(user.getPoint()).isEqualTo(10000);
    }

    @DisplayName("포인트 사용 시 잔액이 부족한 경우 예외가 발생한다.")
    @Test
    void usePoint_whenInsufficientPoint_thenThrowException() {
        int amount = 75000;

        assertThatThrownBy(() -> user.usePoint(amount))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_POINTS)
                .extracting("errorCode")
                .extracting("status", "message")
                .containsExactly(HttpStatus.PAYMENT_REQUIRED, "잔액이 부족합니다.");
    }

}