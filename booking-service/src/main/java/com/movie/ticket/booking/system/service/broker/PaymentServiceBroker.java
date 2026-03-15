package com.movie.ticket.booking.system.service.broker;

import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// NOTE: PaymentServiceBroker is kept as Feign fallback (direct REST call).
// The primary flow now goes through Kafka. This can be used for synchronous fallback.
@FeignClient(name = "payment-service")
public interface PaymentServiceBroker {

    @PostMapping("/payments")
    BookingDTO makePayment(@RequestBody BookingDTO bookingDTO);
}
