package com.roman.price_service.pricesetting.controller;

import com.roman.price_service.pricesetting.dto.PriceResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.roman.price_service.pricesetting.model.Price;
import com.roman.price_service.pricesetting.dto.PriceRequest;
import com.roman.price_service.pricesetting.service.PriceService;
import com.roman.price_service.pricesetting.mapper.PriceMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/priceSetting")
public class PriceController {

    //I get myself the service in
    private final PriceService priceService;
    //setting the controller
    public PriceController(PriceService priceService){

        this.priceService = priceService;
    }
    //I set a post endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<PriceResponse> createPrice(@Valid @RequestBody PriceRequest priceRequest){
        System.out.println("DTO bookingType = " + priceRequest.getBookingType());
        System.out.println("DTO roomType = " + priceRequest.getRoomType());
        Price price = priceService.priceSetting(priceRequest);
        PriceResponse response = PriceMapper.toResponse(price);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{priceId}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable Long priceId,
            @Valid @RequestBody PriceRequest priceRequest){
        // Controller passes both ID + DTO to service
        Price updated = priceService.updatePrice(priceId, priceRequest);

        PriceResponse response = PriceMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{priceId}")
    public ResponseEntity<PriceResponse> findOne(@PathVariable Long priceId){
        Price price = priceService.findOrThrow(priceId);
        PriceResponse response = PriceMapper.toResponse(price);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PriceResponse>> findAllPrices(){
        List<Price> prices = priceService.findAll();
        List<PriceResponse> responses = prices.stream().map(PriceMapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long priceId){
        priceService.deletePrice(priceId);
        return ResponseEntity.noContent().build();
    }
}

