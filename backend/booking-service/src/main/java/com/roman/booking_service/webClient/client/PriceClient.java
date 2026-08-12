package com.roman.booking_service.webClient.client;

import com.roman.booking_service.webClient.dto.PriceResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;

public class PriceClient {
    private final WebClient webClient;
    public PriceClient(WebClient priceWebClient){
        this.webClient = priceWebClient;
    }
    public Optional<PriceResponse> findByRoomTypeAndBookingType(String roomType, String bookingType) {

        try {
            PriceResponse response = webClient.get()
                    .uri("/price/{roomType}/{bookingType}", roomType, bookingType)
                    .retrieve()
                    .bodyToMono(PriceResponse.class)
                    .timeout(Duration.ofSeconds(2))
                    .retryWhen(Retry.max(1))
                    .block();

            return Optional.ofNullable(response);
        }
        catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }


}
