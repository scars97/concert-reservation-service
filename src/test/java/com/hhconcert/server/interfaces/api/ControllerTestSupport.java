package com.hhconcert.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.interfaces.api.concert.ConcertController;
import com.hhconcert.server.interfaces.api.payment.PaymentController;
import com.hhconcert.server.interfaces.api.point.PointController;
import com.hhconcert.server.interfaces.api.queues.QueueController;
import com.hhconcert.server.interfaces.api.reservation.ReservationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        ConcertController.class,
        QueueController.class,
        ReservationController.class,
        PointController.class,
        PaymentController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
