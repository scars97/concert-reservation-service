package com.hhconcert.server.infrastructure.user;

import com.hhconcert.server.business.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, String> {
}
