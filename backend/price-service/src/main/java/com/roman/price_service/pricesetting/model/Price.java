package com.roman.price_service.pricesetting.model;
import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name="prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="booking_type", nullable = false)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name="netto_price", nullable = false)
    private BigDecimal nettoPrice;

    @Column(name="tax_rate", nullable = false)
    private BigDecimal taxRate;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

}

