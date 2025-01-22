package com.hhconcert.server.business.domain.user.service;

import com.hhconcert.server.business.domain.user.dto.PointCommand;
import com.hhconcert.server.business.domain.user.dto.PointInfo;
import com.hhconcert.server.business.domain.user.dto.UserInfo;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserInfo findUser(String userId) {
        return UserInfo.from(userRepository.findUser(userId));
    }

    public PointInfo chargePoint(PointCommand command) {
        User user = userRepository.findUser(command.userId());

        user.chargePoint(command.amount());

        return PointInfo.from(user);
    }

}
