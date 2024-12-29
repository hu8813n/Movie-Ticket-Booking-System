package com.movie.ticket.booking.system.payment.service.impl;


import com.movie.ticket.booking.system.commons.constants.LoggerConstants;
import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.commons.dto.BookingStatus;
import com.movie.ticket.booking.system.payment.service.IPaymentService;
import com.movie.ticket.booking.system.payment.service.entity.PaymentEntity;
import com.movie.ticket.booking.system.payment.service.entity.PaymentStatus;
import com.movie.ticket.booking.system.payment.service.repository.PaymentRepository;
import com.movie.ticket.booking.system.payment.service.stripe.StripePaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private StripePaymentGateway stripePaymentGateway;
//    @Autowired
//    private PaymentServiceKafkaPublisher paymentServiceKafkaPublisher;

    @Transactional
    @Override
    public BookingDTO processPayment(BookingDTO bookingDTO) {
        log.info("received booking details in payment service with data {}",bookingDTO.toString());
        log.info(LoggerConstants.ENTERED_SERVICE_MESSAGE.getValue(),"createPayment",this.getClass());
        PaymentEntity paymentEntity= PaymentEntity.builder()
                .paymentAmount(bookingDTO.getBookingAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .bookingId(bookingDTO.getBookingId())
                .build();
        this.paymentRepository.save(paymentEntity);
        //make a call to payment gateway
        this.stripePaymentGateway.makePayment(bookingDTO);
        paymentEntity.setPaymentTimestamp(LocalDateTime.now());

        if(bookingDTO.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            paymentEntity.setPaymentStatus(PaymentStatus.APPROVED);
        }
        else{
            paymentEntity.setPaymentStatus(PaymentStatus.FAILED);
            bookingDTO.setBookingStatus(BookingStatus.CANCELLED);
        }
        return bookingDTO;
      //  this.paymentServiceKafkaPublisher.publishDataToPaymentResponseTopic(bookingDTO);

    }


//    public void createPayment(BookingDTO bookingDTO){
//
//        log.info(LoggerConstants.ENTERED_SERVICE_MESSAGE.getValue(),"create payment", this.getClass());
//
//        PaymentDto.builder()
//                .bookingId(bookingDTO.getBookingId())
//                .paymentAmount(bookingDTO.getBookingAmount())
//                .paymentStatus(PaymentStatus.PENDING);
//    }
}
