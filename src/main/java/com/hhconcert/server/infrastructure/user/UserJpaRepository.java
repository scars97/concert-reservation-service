package com.hhconcert.server.infrastructure.user;

import com.hhconcert.server.business.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);
}
