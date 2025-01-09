package com.hhconcert.server.infrastructure.user;

import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class UserCoreRepository implements UserRepository {

    private final UserJpaRepository repository;

    @Override
    public User findUser(String userId) {
        return repository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 사용자입니다."));
    }

}
