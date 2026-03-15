package com.movie.ticket.booking.system.payment.service.impl;

import com.movie.ticket.booking.system.commons.constants.LoggerConstants;
import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.commons.dto.BookingStatus;
import com.movie.ticket.booking.system.payment.service.IPaymentService;
import com.movie.ticket.booking.system.payment.service.entity.PaymentEntity;
import com.movie.ticket.booking.system.payment.service.entity.PaymentStatus;
import com.movie.ticket.booking.system.payment.service.kafka.publisher.PaymentServiceKafkaPublisher;
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

    // Fixed: wired in and activated (was commented out)
    @Autowired
    private PaymentServiceKafkaPublisher paymentServiceKafkaPublisher;

    @Transactional
    @Override
    public BookingDTO processPayment(BookingDTO bookingDTO) {
        log.info("Received booking details in payment service with bookingId: {}", bookingDTO.getBookingId());
        log.info(LoggerConstants.ENTERED_SERVICE_MESSAGE.getValue(), "processPayment", this.getClass(), bookingDTO);

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .paymentAmount(bookingDTO.getBookingAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .bookingId(bookingDTO.getBookingId())
                .paymentTimestamp(LocalDateTime.now())
                .build();
        this.paymentRepository.save(paymentEntity);

        // Call Stripe payment gateway - sets CONFIRMED or leaves status unchanged on failure
        this.stripePaymentGateway.makePayment(bookingDTO);

        // Fixed: guard against null status (gateway exception leaves it null)
        if (bookingDTO.getBookingStatus() != null &&
                bookingDTO.getBookingStatus().equals(BookingStatus.CONFIRMED)) {
            paymentEntity.setPaymentStatus(PaymentStatus.APPROVED);
        } else {
            paymentEntity.setPaymentStatus(PaymentStatus.FAILED);
            bookingDTO.setBookingStatus(BookingStatus.CANCELLED);
        }

        // Fixed: persist updated payment status (original code never re-saved)
        this.paymentRepository.save(paymentEntity);

        // Fixed: publish result back to booking-service via Kafka (was commented out)
        this.paymentServiceKafkaPublisher.publishDataToPaymentResponseTopic(bookingDTO);

        return bookingDTO;
    }
}
