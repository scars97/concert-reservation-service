package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.PointResult;
import com.hhconcert.server.application.facade.UserPointFacade;
import com.hhconcert.server.business.user.domain.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointFacadeTest extends IntegrationTestSupport {

    @Autowired
    private UserPointFacade userPointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @DisplayName("포인트가 충전된다.")
    @Test
    void chargePoint() {
        User user = userJpaRepository.save(new User("test1234", 80000));

        PointResult result = userPointFacade.chargePoint(new PointRequest("test1234", 20000));

        assertThat(result).extracting("userId", "point")
                .containsExactly("test1234", 100000);
    }

    @DisplayName("포인트가 조회된다.")
    @Test
    void getPoint() {
        User user = userJpaRepository.save(new User("asdf1234", 10000));

        PointResult result = userPointFacade.getPoint(new UserRequest("asdf1234"));

        assertThat(result).extracting("userId", "point")
                .containsExactly("asdf1234", 10000);
    }
}