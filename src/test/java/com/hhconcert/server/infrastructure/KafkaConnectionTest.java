package com.hhconcert.server.infrastructure;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;

@ActiveProfiles("test")
@SpringBootTest
class KafkaConnectionTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaConnectionTest.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private String receiveMessage;

    @DisplayName("Kafka 메시지 발행에 성공한다.")
    @Test
    void testKafkaMessageSending() {
        String topic = "test-topic";
        String message = "test-message";

        kafkaTemplate.send(topic, message);
        kafkaTemplate.flush();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    Assertions.assertThat(receiveMessage).isEqualTo(message);
                });
    }

    @KafkaListener(
        topics = "test-topic",
        groupId = "test-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTest(String message) {
        log.info("message consume : {}", message);
        this.receiveMessage = message;
    }
}
