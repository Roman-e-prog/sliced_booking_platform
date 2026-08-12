package com.roman.price_service.pricesetting.mapper;

import com.roman.price_service.pricesetting.dto.PriceResponse;
import com.roman.price_service.pricesetting.model.Price;

public class PriceMapper {
    public static PriceResponse toResponse(Price price) {
        return new PriceResponse(
                price.getId(),
                price.getRoomType(),
                price.getBookingType(),
                price.getNettoPrice(),
                price.getTaxRate()
        );
    }
}
