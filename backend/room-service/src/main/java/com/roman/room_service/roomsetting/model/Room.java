package com.roman.room_service.roomsetting.model;


import com.roman.room_service.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="room_type", nullable = false)
    private RoomType roomType;

    @Column(name="is_available", insertable = false)
    private Boolean isAvailable;

    private String description;

    @Column(name="price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false)
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImages> images = new ArrayList<>();
}

