package com.hhconcert.server.business.domain.user;

import com.hhconcert.server.business.domain.user.dto.PointCommand;
import com.hhconcert.server.business.domain.user.dto.PointInfo;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import com.hhconcert.server.business.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = new User("test1234", 10000);
    }

    @DisplayName("사용자의 포인트가 충전된다.")
    @Test
    void chargePoint() {
        when(userRepository.findUser("test1234")).thenReturn(user);

        PointInfo result = userService.chargePoint(new PointCommand("test1234", 20000));

        assertThat(result.userId()).isEqualTo("test1234");
        assertThat(result.point()).isEqualTo(30000);
    }

}