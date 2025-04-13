package com.hhconcert.server.infrastructure.persistence.core;

import com.hhconcert.server.business.user.domain.User;
import com.hhconcert.server.business.user.persistance.UserRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
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
