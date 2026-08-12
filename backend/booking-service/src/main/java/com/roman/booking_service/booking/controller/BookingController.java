package com.roman.booking_service.booking.controller;

import com.roman.booking_service.booking.dto.BookingRequest;
import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.booking.mapper.BookingMapper;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper){
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping("/")
    public ResponseEntity<BookingResponse> bookRoom(
            @Valid @RequestBody BookingRequest bookingRequest){

        Booking booking = bookingService.bookRoom(bookingRequest);
        BookingResponse response = bookingMapper.toResponse(booking);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.findByIdOrThrow(bookingId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingRequest bookingRequest){

        Booking updated = bookingService.updateBooking(bookingId, bookingRequest);
        BookingResponse response = bookingMapper.toResponse(updated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId){
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
