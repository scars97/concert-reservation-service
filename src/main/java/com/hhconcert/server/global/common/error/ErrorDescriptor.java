package com.hhconcert.server.global.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorDescriptor {

    HttpStatus getStatus();

    String getMessage();
}
