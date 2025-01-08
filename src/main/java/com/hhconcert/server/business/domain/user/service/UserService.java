package com.hhconcert.server.business.domain.user.service;

import com.hhconcert.server.business.domain.user.dto.UserResult;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResult findUser(String userId) {
        return UserResult.from(userRepository.findUser(userId));
    }

}
