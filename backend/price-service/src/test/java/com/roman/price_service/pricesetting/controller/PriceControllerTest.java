package com.roman.price_service.pricesetting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.price_service.pricesetting.dto.PriceRequest;
import com.roman.price_service.pricesetting.dto.PriceResponse;
import com.roman.price_service.pricesetting.model.Price;
import com.roman.price_service.pricesetting.service.PriceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceService priceService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // CREATE PRICE (POST) – requires ADMIN
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void should_create_price() throws Exception {

        Price price = new Price();
        price.setId(1L);
        price.setRoomType(com.roman.price_service.enums.RoomType.ONE_BED);
        price.setBookingType(com.roman.price_service.enums.BookingType.WITH_BREAKFAST);
        price.setNettoPrice(BigDecimal.valueOf(250));
        price.setTaxRate(BigDecimal.valueOf(19));

        when(priceService.priceSetting(any(PriceRequest.class))).thenReturn(price);

        PriceRequest request = new PriceRequest();
        request.setRoomType(com.roman.price_service.enums.RoomType.ONE_BED);
        request.setBookingType(com.roman.price_service.enums.BookingType.WITH_BREAKFAST);
        request.setNettoPrice(BigDecimal.valueOf(250));
        request.setTaxRate(BigDecimal.valueOf(19));

        mockMvc.perform(
                post("/api/priceSetting/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceId").value(1))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"))
                .andExpect(jsonPath("$.bookingType").value("WITH_BREAKFAST"))
                .andExpect(jsonPath("$.nettoPrice").value(250))
                .andExpect(jsonPath("$.taxRate").value(19));
    }

    // ---------------------------------------------------------
    // UPDATE PRICE (PUT) – requires ADMIN
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void should_update_price() throws Exception {

        Price updated = new Price();
        updated.setId(1L);
        updated.setRoomType(com.roman.price_service.enums.RoomType.TWO_BED);
        updated.setBookingType(com.roman.price_service.enums.BookingType.ONLY_REST);
        updated.setNettoPrice(BigDecimal.valueOf(300));
        updated.setTaxRate(BigDecimal.valueOf(19));

        when(priceService.updatePrice(eq(1L), any(PriceRequest.class))).thenReturn(updated);

        PriceRequest request = new PriceRequest();
        request.setRoomType(com.roman.price_service.enums.RoomType.TWO_BED);
        request.setBookingType(com.roman.price_service.enums.BookingType.ONLY_REST);
        request.setNettoPrice(BigDecimal.valueOf(300));
        request.setTaxRate(BigDecimal.valueOf(19));

        mockMvc.perform(
                put("/api/priceSetting/{priceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceId").value(1))
                .andExpect(jsonPath("$.roomType").value("TWO_BED"))
                .andExpect(jsonPath("$.bookingType").value("ONLY_REST"))
                .andExpect(jsonPath("$.nettoPrice").value(300))
                .andExpect(jsonPath("$.taxRate").value(19));
    }

    // ---------------------------------------------------------
    // GET ONE PRICE – public
    // ---------------------------------------------------------
    @Test
    void should_find_one_price() throws Exception {

        Price price = new Price();
        price.setId(1L);
        price.setRoomType(com.roman.price_service.enums.RoomType.ONE_BED);
        price.setBookingType(com.roman.price_service.enums.BookingType.WITH_BREAKFAST);
        price.setNettoPrice(BigDecimal.valueOf(250));
        price.setTaxRate(BigDecimal.valueOf(19));

        when(priceService.findOrThrow(1L)).thenReturn(price);

        mockMvc.perform(get("/api/priceSetting/{priceId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceId").value(1))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"))
                .andExpect(jsonPath("$.bookingType").value("WITH_BREAKFAST"))
                .andExpect(jsonPath("$.nettoPrice").value(250))
                .andExpect(jsonPath("$.taxRate").value(19));
    }

    // ---------------------------------------------------------
    // GET ALL PRICES – public
    // ---------------------------------------------------------
    @Test
    void should_find_all_prices() throws Exception {

        Price p1 = new Price();
        p1.setId(1L);
        p1.setRoomType(com.roman.price_service.enums.RoomType.ONE_BED);
        p1.setBookingType(com.roman.price_service.enums.BookingType.WITH_BREAKFAST);
        p1.setNettoPrice(BigDecimal.valueOf(250));
        p1.setTaxRate(BigDecimal.valueOf(19));

        Price p2 = new Price();
        p2.setId(2L);
        p2.setRoomType(com.roman.price_service.enums.RoomType.TWO_BED);
        p2.setBookingType(com.roman.price_service.enums.BookingType.ONLY_REST);
        p2.setNettoPrice(BigDecimal.valueOf(300));
        p2.setTaxRate(BigDecimal.valueOf(19));

        when(priceService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/priceSetting/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---------------------------------------------------------
    // DELETE PRICE – requires ADMIN
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void should_delete_price() throws Exception {

        doNothing().when(priceService).deletePrice(1L);

        mockMvc.perform(delete("/api/priceSetting/{priceId}", 1L))
                .andExpect(status().isNoContent());
    }
}
