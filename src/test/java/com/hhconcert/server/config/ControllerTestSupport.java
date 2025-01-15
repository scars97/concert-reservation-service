package com.hhconcert.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.application.facade.*;
import com.hhconcert.server.fixture.ControllerTestFixture;
import com.hhconcert.server.global.interceptor.TokenValidationInterceptor;
import com.hhconcert.server.interfaces.api.concert.ConcertController;
import com.hhconcert.server.interfaces.api.payment.PaymentController;
import com.hhconcert.server.interfaces.api.point.PointController;
import com.hhconcert.server.interfaces.api.queues.QueueController;
import com.hhconcert.server.interfaces.api.reservation.ReservationController;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        ConcertController.class,
        QueueController.class,
        ReservationController.class,
        PointController.class,
        PaymentController.class
})
public class ControllerTestSupport {

    protected static final String TEST_TOKEN = "Bearer TEST_TOKEN";

    protected ControllerTestFixture fixture = new ControllerTestFixture();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected TokenValidationInterceptor interceptor;

    @MockitoBean
    protected ConcertFacade concertFacade;

    @MockitoBean
    protected PaymentFacade paymentFacade;

    @MockitoBean
    protected QueueFacade queueFacade;

    @MockitoBean
    protected UserPointFacade userPointFacade;

    @MockitoBean
    protected ReservationFacade reservationFacade;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.when(interceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    }

}
