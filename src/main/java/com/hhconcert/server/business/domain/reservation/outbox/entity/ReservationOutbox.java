package com.hhconcert.server.business.domain.reservation.outbox.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.application.event.reservation.ReserveSuccessEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservationoutbox")
public class ReservationOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Long reserveId;

    @Lob
    private String payload;

    @Setter
    private LocalDateTime publishedAt;

    @Transient
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ReservationOutbox(Long reserveId, OutboxStatus status, LocalDateTime publishedAt) {
        this.reserveId = reserveId;
        this.status = status;
        this.publishedAt = publishedAt;
    }

    public static ReservationOutbox init(Long reserveId) {
        return new ReservationOutbox(reserveId, OutboxStatus.INIT, LocalDateTime.now());
    }

    public void updateForPublished() {
        this.status = OutboxStatus.PUBLISHED;
    }

    public void toPayload(ReserveSuccessEvent event) {
        try {
            this.payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialisation failure", e);
        }
    }

    public ReserveSuccessEvent toReserveSuccessEvent() {
        try {
            return objectMapper.readValue(this.payload, ReserveSuccessEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse payload", e);
        }
    }

}
