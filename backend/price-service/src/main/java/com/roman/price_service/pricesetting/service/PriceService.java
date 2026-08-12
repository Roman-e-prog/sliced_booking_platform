package com.roman.price_service.pricesetting.service;

import com.roman.price_service.exceptions.NotFoundException;
import com.roman.price_service.pricesetting.dto.PriceRequest;
import com.roman.price_service.pricesetting.model.Price;
import com.roman.price_service.pricesetting.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceService {
    //define the final variable for repo
    private final PriceRepository priceRepository;
    //set repo into service
    public PriceService(PriceRepository priceRepository){

        this.priceRepository = priceRepository;
    }

    //find or throw error
    public Price findOrThrow(Long priceId){
        assert priceRepository != null;
        return priceRepository.findById(priceId)
                .orElseThrow(()->new NotFoundException(priceId, "Price"));
    }
    //save
    public Price priceSetting(PriceRequest priceRequest){
        Price price = new Price();
        System.out.println("Incoming bookingType: " + priceRequest.getBookingType());
        System.out.println("Incoming roomType: " + priceRequest.getRoomType());
        price.setRoomType(priceRequest.getRoomType()); // COMMENT: enum now
        price.setBookingType(priceRequest.getBookingType());
        price.setNettoPrice(priceRequest.getNettoPrice());
        price.setTaxRate(priceRequest.getTaxRate());
        return priceRepository.save(price);
    }
    //update
    public Price updatePrice(Long priceId, PriceRequest request){
        Price price = findOrThrow(priceId);
        price.setRoomType(request.getRoomType());
        price.setBookingType(request.getBookingType());
        price.setNettoPrice(request.getNettoPrice());
        price.setTaxRate(request.getTaxRate());
        return priceRepository.save(price);
    }
    public List<Price> findAll(){
        return priceRepository.findAll();
    }
    public void deletePrice(Long priceId){
        Price price = findOrThrow(priceId);
        priceRepository.delete(price);

    }
}

