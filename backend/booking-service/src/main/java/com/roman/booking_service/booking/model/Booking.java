package com.roman.booking_service.booking.model;

import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.UserType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.webClient.dto.UserResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Setter
@Getter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "number_of_persons", nullable = false)
    private Integer numberOfPersons;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @Column(name = "full_price", nullable = false)
    private BigDecimal fullPrice;

    @Column(nullable = false)
    private BigDecimal tax;

    @Column(name = "brutto_price", nullable = false)
    private BigDecimal bruttoPrice;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
