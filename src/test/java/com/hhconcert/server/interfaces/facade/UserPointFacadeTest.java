package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Sql("classpath:test-data.sql")
class UserPointFacadeTest {

    @Autowired
    private UserPointFacade userPointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @DisplayName("포인트가 충전된다.")
    @Test
    void chargePoint() {
        User user = userJpaRepository.save(new User("test1234", 80000));

        PointResponse response = userPointFacade.chargePoint(new PointRequest("test1234", 20000));

        assertThat(response).extracting("userId", "point")
                .containsExactly("test1234", 100000);
    }

    @DisplayName("포인트가 조회된다.")
    @Test
    void getPoint() {
        User user = userJpaRepository.save(new User("asdf1234", 10000));

        PointResponse response = userPointFacade.getPoint("asdf1234");

        assertThat(response).extracting("userId", "point")
                .containsExactly("asdf1234", 10000);
    }
}