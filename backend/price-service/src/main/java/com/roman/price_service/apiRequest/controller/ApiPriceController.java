package com.roman.price_service.apiRequest.controller;

import com.roman.price_service.enums.RoomType;
import com.roman.price_service.enums.BookingType;
import com.roman.price_service.pricesetting.model.Price;
import com.roman.price_service.pricesetting.repository.PriceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/prices")
public class ApiPriceController {

    private final PriceRepository priceRepository;

    public ApiPriceController(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("/{roomType}")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable String roomType) {

        RoomType type;
        try {
            type = RoomType.valueOf(roomType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return priceRepository.findByRoomType(type)
                .map(price -> ResponseEntity.ok(price.getNettoPrice()))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{roomType}/{bookingType}")
    public ResponseEntity<Price> getRoomData(
            @PathVariable RoomType roomType,
            @PathVariable BookingType bookingType) {

        return priceRepository.findByRoomTypeAndBookingType(roomType, bookingType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}

