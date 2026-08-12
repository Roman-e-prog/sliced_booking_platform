package com.roman.price_service.pricesetting.repository;
import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;
import com.roman.price_service.pricesetting.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    // COMMENT: find price by room type and booking type and generally I set here only custom requests
    Optional<Price> findByRoomTypeAndBookingType(
            RoomType roomType,
            BookingType bookingType
    );
    Optional<Price> findByRoomType(
            RoomType roomType
    );

}
