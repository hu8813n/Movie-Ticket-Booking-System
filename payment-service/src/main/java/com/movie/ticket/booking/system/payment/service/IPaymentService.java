package com.movie.ticket.booking.system.payment.service;

import com.movie.ticket.booking.system.commons.dto.BookingDTO;

public interface IPaymentService {
    BookingDTO processPayment(BookingDTO bookingDTO);
}