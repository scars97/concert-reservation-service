package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.infrastructure.queues.TokenJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class TokenServiceIntegrateTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    private final static int MAX_ACTIVE_TOKEN_COUNT = 10;
    String testTokenId;

    @AfterEach
    void tearDown() {
        tokenJpaRepository.deleteAllInBatch();
    }

}