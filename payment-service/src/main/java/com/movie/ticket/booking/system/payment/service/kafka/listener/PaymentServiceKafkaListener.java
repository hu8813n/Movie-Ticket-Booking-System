package com.movie.ticket.booking.system.payment.service.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.payment.service.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceKafkaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IPaymentService paymentService;

    /**
     * Listens to the payment-request topic published by booking-service.
     * Processes the payment and publishes the result to payment-response topic.
     */
    @KafkaListener(topics = "payment-request", groupId = "paymentrequest1")
    public void subscribePaymentRequestTopic(String bookingDTOJson) {
        log.info("Received booking details from payment-request kafka topic");
        try {
            BookingDTO bookingDTO = objectMapper.readValue(bookingDTOJson, BookingDTO.class);
            this.paymentService.processPayment(bookingDTO);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing booking details from payment-request topic: {}",
                    e.getMessage());
        }
    }
}
