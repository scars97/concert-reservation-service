package com.hhconcert.server.business.domain.queues.entity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class TokenGenerator {

    public static String generateToken(String userId) {
        String encodedUserId = Base64.getEncoder().encodeToString(userId.getBytes(StandardCharsets.UTF_8));

        return encodedUserId + "-" + UUID.nameUUIDFromBytes(userId.getBytes());
    }

    public static String tokenIdToUserId(String tokenId) {
        String[] parts = tokenId.split("-");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid encoded UUID format");
        }

        return new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
    }

}
