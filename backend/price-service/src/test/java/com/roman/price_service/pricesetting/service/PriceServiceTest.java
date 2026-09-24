package com.roman.price_service.pricesetting.service;

import com.roman.price_service.exceptions.NotFoundException;
import com.roman.price_service.pricesetting.dto.PriceRequest;
import com.roman.price_service.pricesetting.model.Price;
import com.roman.price_service.pricesetting.repository.PriceRepository;
import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class PriceServiceTest {

    @MockitoBean
    private PriceRepository priceRepository;

    @Test
    void should_find_price_or_throw() {
        Price price = new Price();
        price.setId(1L);
        price.setRoomType(RoomType.ONE_BED);
        price.setBookingType(BookingType.WITH_BREAKFAST);
        price.setNettoPrice(BigDecimal.valueOf(250));
        price.setTaxRate(BigDecimal.valueOf(19));

        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        PriceService service = new PriceService(priceRepository);

        Price result = service.findOrThrow(1L);

        assertEquals(1L, result.getId());
        assertEquals(RoomType.ONE_BED, result.getRoomType());
        assertEquals(BookingType.WITH_BREAKFAST, result.getBookingType());
    }

    @Test
    void should_throw_when_price_not_found() {
        when(priceRepository.findById(99L)).thenReturn(Optional.empty());

        PriceService service = new PriceService(priceRepository);

        assertThrows(NotFoundException.class, () -> service.findOrThrow(99L));
    }

    @Test
    void should_create_price() {
        Price saved = new Price();
        saved.setId(1L);
        saved.setRoomType(RoomType.ONE_BED);
        saved.setBookingType(BookingType.WITH_BREAKFAST);
        saved.setNettoPrice(BigDecimal.valueOf(250));
        saved.setTaxRate(BigDecimal.valueOf(19));

        when(priceRepository.save(any())).thenReturn(saved);

        PriceRequest request = new PriceRequest();
        request.setRoomType(RoomType.ONE_BED);
        request.setBookingType(BookingType.WITH_BREAKFAST);
        request.setNettoPrice(BigDecimal.valueOf(250));
        request.setTaxRate(BigDecimal.valueOf(19));

        PriceService service = new PriceService(priceRepository);

        Price result = service.priceSetting(request);

        assertEquals(1L, result.getId());
        assertEquals(RoomType.ONE_BED, result.getRoomType());
        assertEquals(BookingType.WITH_BREAKFAST, result.getBookingType());
        assertEquals(BigDecimal.valueOf(250), result.getNettoPrice());
        assertEquals(BigDecimal.valueOf(19), result.getTaxRate());
    }

    @Test
    void should_update_price() {
        Price existing = new Price();
        existing.setId(1L);
        existing.setRoomType(RoomType.ONE_BED);
        existing.setBookingType(BookingType.ONLY_REST);
        existing.setNettoPrice(BigDecimal.valueOf(200));
        existing.setTaxRate(BigDecimal.valueOf(19));

        when(priceRepository.findById(1L)).thenReturn(Optional.of(existing));

        Price updated = new Price();
        updated.setId(1L);
        updated.setRoomType(RoomType.TWO_BED);
        updated.setBookingType(BookingType.WITH_BREAKFAST);
        updated.setNettoPrice(BigDecimal.valueOf(300));
        updated.setTaxRate(BigDecimal.valueOf(19));

        when(priceRepository.save(any())).thenReturn(updated);

        PriceRequest request = new PriceRequest();
        request.setRoomType(RoomType.TWO_BED);
        request.setBookingType(BookingType.WITH_BREAKFAST);
        request.setNettoPrice(BigDecimal.valueOf(300));
        request.setTaxRate(BigDecimal.valueOf(19));

        PriceService service = new PriceService(priceRepository);

        Price result = service.updatePrice(1L, request);

        assertEquals(1L, result.getId());
        assertEquals(RoomType.TWO_BED, result.getRoomType());
        assertEquals(BookingType.WITH_BREAKFAST, result.getBookingType());
        assertEquals(BigDecimal.valueOf(300), result.getNettoPrice());
    }

    @Test
    void should_find_all_prices() {
        Price p1 = new Price();
        p1.setId(1L);
        p1.setRoomType(RoomType.ONE_BED);

        Price p2 = new Price();
        p2.setId(2L);
        p2.setRoomType(RoomType.TWO_BED);

        when(priceRepository.findAll()).thenReturn(List.of(p1, p2));

        PriceService service = new PriceService(priceRepository);

        List<Price> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void should_delete_price() {
        Price existing = new Price();
        existing.setId(1L);

        when(priceRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(priceRepository).delete(existing);

        PriceService service = new PriceService(priceRepository);

        service.deletePrice(1L);

        verify(priceRepository).delete(existing);
    }
}
