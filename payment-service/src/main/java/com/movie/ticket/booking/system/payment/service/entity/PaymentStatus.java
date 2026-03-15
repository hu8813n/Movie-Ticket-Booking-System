package com.movie.ticket.booking.system.payment.service.entity;

public enum PaymentStatus {
    PENDING,  // The booking request is pending payment confirmation
    APPROVED, // Payment has been successfully processed and confirmed
    REFUND, // Payment was successfully refunded for a canceled booking
    FAILED    // Payment processing failed, and the user needs to retry the payment
}
