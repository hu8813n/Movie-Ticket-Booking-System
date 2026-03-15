package com.movie.ticket.booking.system.payment.service.dto;

//import com.movie.ticket.booking.system.commons.dto.PaymentStatus;
import com.movie.ticket.booking.system.payment.service.entity.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentDto {
    private UUID bookingId;
    private PaymentStatus paymentStatus;
    private Double paymentAmount;
    private LocalDateTime paymentTimeStamp;

}
