package com.hhconcert.server.business.user.persistance;

import com.hhconcert.server.business.user.domain.User;

public interface UserRepository {

    User findUser(String userId);

}
