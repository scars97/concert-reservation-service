package com.hhconcert.server.business.domain.queues.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TokenGenerator {

    public static String generateToken(String userId) {
        return UUID.nameUUIDFromBytes(userId.getBytes()).toString();
    }

}
