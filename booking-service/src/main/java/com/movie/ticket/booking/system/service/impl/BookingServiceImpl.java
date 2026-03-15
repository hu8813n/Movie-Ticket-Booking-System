package com.movie.ticket.booking.system.service.impl;

import com.movie.ticket.booking.system.commons.dto.BookingDTO;
import com.movie.ticket.booking.system.commons.dto.BookingStatus;
import com.movie.ticket.booking.system.service.IBookingService;
import com.movie.ticket.booking.system.service.broker.PaymentServiceBroker;
import com.movie.ticket.booking.system.service.entity.BookingEntity;
import com.movie.ticket.booking.system.service.kafka.publisher.BookingServiceKafkaPublisher;
import com.movie.ticket.booking.system.service.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingServiceImpl implements IBookingService {

    @Autowired
    private PaymentServiceBroker paymentServiceBroker;
    @Autowired
    private BookingServiceKafkaPublisher bookingServiceKafkaPublisher;

    @Autowired
    private BookingRepository bookingRepository;
    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        // Implement logic to create booking
       BookingEntity bookingEntity =  BookingEntity.builder()
                .bookingAmount(bookingDTO.getBookingAmount())
                .seatsBooked(bookingDTO.getSeatsBooked())
                .bookingStatus(BookingStatus.PENDING)
                .movieId(bookingDTO.getMovieId())
                .userId(bookingDTO.getUserId())
                .showDate(bookingDTO.getShowDate())
                .showTime(bookingDTO.getShowTime())
                .build();

        bookingRepository.save(bookingEntity);
        bookingDTO.setBookingId(bookingEntity.getBookingId());
        bookingDTO.setBookingStatus(BookingStatus.PENDING);
        this.bookingServiceKafkaPublisher.publishPaymentRequestTopicToPaymentService(bookingDTO);
        return bookingDTO;




//        //call the payment method
//       bookingDTO =  this.paymentServiceBroker.makePayment(bookingDTO);
//        //after payment service done its job, we need to get updated bookingDTO
//       bookingEntity.setBookingStatus(bookingDTO.getBookingStatus());
    //   this.bookingRepository.save(bookingEntity); //if method is annotated with @transactional we dont have to save  again
//        return BookingDTO.builder()
//                .bookingId(bookingEntity.getBookingId())
//                .bookingAmount(bookingEntity.getBookingAmount())
//                .bookingStatus(bookingEntity.getBookingStatus())
//                .movieId(bookingEntity.getMovieId())
//                .showTime(bookingEntity.getShowTime())
//                .showDate(bookingEntity.getShowDate())
//                .userId(bookingEntity.getUserId())
//                .seatsBooked(bookingEntity.getSeatsBooked( ))
//                .build();

    }


    @Override
    @Transactional
    public void processBooking(BookingDTO bookingDTO) {
        Optional<BookingEntity> bookingEntityOptional= this.bookingRepository.findById(bookingDTO.getBookingId());
        if(bookingEntityOptional.isPresent()){
            BookingEntity bookingEntity = bookingEntityOptional.get();
            bookingEntity.setBookingStatus(bookingDTO.getBookingStatus());
        }
    }

}
