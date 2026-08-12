package com.roman.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback/booking", produces = MediaType.APPLICATION_JSON_VALUE)
    public String bookingFallback() {

        log.warn("Fallback triggered for booking-service");

        return """
            {
              "status": 503,
              "message": "Booking service temporarily unavailable",
              "fallback": true
            }
            """;
    }
}
