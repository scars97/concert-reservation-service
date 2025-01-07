package com.hhconcert.server.business.domain.concert.persistance;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();
        concertJpaRepository.saveAll(List.of(
                new Concert("콘서트1", now, now.plusDays(1)),
                new Concert("콘서트2", now.plusDays(1), now.plusDays(2))
        ));
    }

    @AfterEach
    void tearDown() {
        concertJpaRepository.deleteAllInBatch();
    }

    @DisplayName("콘서트 목록이 조회된다.")
    @Test
    void getConcerts() {
        LocalDate now = LocalDate.now();

        List<Concert> concerts = concertRepository.getConcerts();

        assertThat(concerts).hasSize(2)
                .extracting("title", "startDate", "endDate")
                .containsExactly(
                        tuple("콘서트1", now, now.plusDays(1)),
                        tuple("콘서트2", now.plusDays(1), now.plusDays(2))
                );
    }

    @DisplayName("단일 콘서트가 조회된다.")
    @Test
    void findConcert() {
        LocalDate now = LocalDate.now();
        Long id = 5L;

        Concert concert = concertRepository.findConcert(id);

        assertThat(concert).extracting("title", "startDate", "endDate")
                .containsExactly("콘서트1", now, now.plusDays(1));
    }

    @DisplayName("등록되지 않은 콘서트 조회 시, 예외가 발생한다.")
    @Test
    void whenInvalidConcert_thenThrowException() {
        Long invalidId = 99L;

        assertThatThrownBy(() -> concertRepository.findConcert(invalidId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 콘서트입니다.");
    }
}