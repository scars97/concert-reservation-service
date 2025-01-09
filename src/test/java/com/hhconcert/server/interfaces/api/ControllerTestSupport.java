package com.hhconcert.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.global.interceptor.TokenValidationInterceptor;
import com.hhconcert.server.interfaces.mockapi.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        MockConcertController.class,
        MockQueueController.class,
        MockReservationController.class,
        MockPointController.class,
        MockPaymentController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private TokenValidationInterceptor interceptor;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.when(interceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    }

}
