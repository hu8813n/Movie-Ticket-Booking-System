package com.movie.ticket.booking.system.service.kafka.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.service.IBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingServiceKafkaListener {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IBookingService bookingService;


    @KafkaListener(topics = "payment-response",groupId = "paymentresponse1")
    public void subscribePaymentResponseTopic(String bookingDTOJson){
        log.info("receiving conformation of booking details from payment-response kafka topic");
        try {
            BookingDTO bookingDTO = objectMapper.readValue(bookingDTOJson, BookingDTO.class);
            this.bookingService.processBooking(bookingDTO);
        } catch (JsonProcessingException e) {
            log.error("Error while receiving conformation of booking details" +
                    " from payment-response kafka topic");
        }

    }


}