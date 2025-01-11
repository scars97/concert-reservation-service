package com.hhconcert.server.business.domain.user.persistance;

import com.hhconcert.server.business.domain.user.entity.User;

public interface UserRepository {

    User findUser(String userId);

}
