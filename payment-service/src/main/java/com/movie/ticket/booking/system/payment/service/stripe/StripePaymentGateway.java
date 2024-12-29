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
//            PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder()
//                    .setAmount(bookingDTO.getBookingAmount().longValue())
//                    .setCurrency("usd")
//                    .setPaymentMethod("pm_card_in")
//                    .build();
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
            Map<String, Object> params = new HashMap<>();
            params.put("amount",(int)Math.round(bookingDTO.getBookingAmount()));
            params.put("currency", "usd");
            params.put("description", "Test Payment");
            params.put("source", "tok_visa ");
            Charge.create(params);
            bookingDTO.setBookingStatus(BookingStatus.CONFIRMED);
        } catch (Exception e) {
            log.error("Payment failed at gateway level with exception : {}", e.getMessage());
        }
    }

}
