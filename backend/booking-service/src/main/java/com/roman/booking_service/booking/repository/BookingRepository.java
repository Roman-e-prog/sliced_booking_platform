package com.roman.booking_service.booking.repository;

import com.roman.booking_service.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    void deleteByUserId(Long aLong);
}
