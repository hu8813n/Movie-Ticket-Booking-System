package com.movie.ticket.booking.system.payment.service.stripe;


import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.commons.dto.BookingStatus;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripePaymentGateway {

    @Value("${stripe.key}")
    private String secretKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = secretKey;
    }
    public void makePayment(BookingDTO bookingDTO) {
        try {
            Map<String, Object> params = new HashMap<>();
            // Fixed: Stripe expects amount in smallest currency unit (cents), not dollars
            params.put("amount", (long) Math.round(bookingDTO.getBookingAmount() * 100));
            params.put("currency", "usd");
            params.put("description", "Movie ticket booking - ID: " + bookingDTO.getBookingId());
            params.put("source", "tok_visa");
            Charge charge = Charge.create(params);
            log.info("Stripe charge created successfully: {}", charge.getId());
            bookingDTO.setBookingStatus(BookingStatus.CONFIRMED);
        } catch (Exception e) {
            log.error("Payment failed at gateway level with exception : {}", e.getMessage());
        }
    }

}
