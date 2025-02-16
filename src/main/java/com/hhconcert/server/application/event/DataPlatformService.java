package com.hhconcert.server.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformService {

    public void send(Long reserveId) {
        try {
            log.info("데이터 플랫폼 전송 성공");
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패 : {}", e.getMessage());
        }
    }
}
