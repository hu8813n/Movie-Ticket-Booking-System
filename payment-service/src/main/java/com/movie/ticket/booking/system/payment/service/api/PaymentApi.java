package com.movie.ticket.booking.system.payment.service.api;

import com.movie.ticket.booking.system.commons.constants.LoggerConstants;
import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.payment.service.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentApi {
    @Autowired
    private IPaymentService paymentService;

    @PostMapping
    public BookingDTO makePayment(@RequestBody BookingDTO bookingDTO){
        log.info(LoggerConstants.ENTERED_CONTROLLER_MESSAGE.getValue(),"create-payment",this.getClass(),bookingDTO.toString());
        return this.paymentService.processPayment(bookingDTO);
    }
}
