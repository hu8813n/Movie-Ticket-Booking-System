package com.movie.ticket.booking.system.api.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BookingControllerFallbackApi {

    @GetMapping("/booking-fallback")
    public String bookingsFallbackApi(){
        return "Booking Service is in maintenance mode. Please try after some time";
    }
}
