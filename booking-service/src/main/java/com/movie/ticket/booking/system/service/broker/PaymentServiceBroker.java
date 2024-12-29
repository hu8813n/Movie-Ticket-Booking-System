package com.movie.ticket.booking.system.service.broker;

import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Component
@FeignClient (name = "payment-service")
public interface PaymentServiceBroker {

    @PostMapping("/payments")
    public BookingDTO makePayment(@RequestBody BookingDTO bookingDTO);
}
