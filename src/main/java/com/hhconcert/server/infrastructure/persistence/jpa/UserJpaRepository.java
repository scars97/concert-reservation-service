package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findByUserId(String userId);

}
