package com.movie.ticket.booking.system.service.entity;

import com.movie.ticket.booking.system.commons.dto.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingEntity {
    @Id // to make this booking_id as a primary key for the booking Entity
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "movie_id")
    private Integer movieId;

    @ElementCollection  //@ElementCollection annotation automatically creates a one-to-many mapping for us.
    private List<String> seatsBooked;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_time")
    private LocalTime showTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(name = "booking_amount")
    private Double bookingAmount;


}
