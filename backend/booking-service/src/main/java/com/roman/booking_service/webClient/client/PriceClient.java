package com.roman.booking_service.webClient.client;

import com.roman.booking_service.webClient.dto.PriceResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.util.retry.Retry;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Optional;
@Component
public class PriceClient {
    private final WebClient webClient;
    public PriceClient(@Qualifier("priceWebClient") WebClient webClient){
        this.webClient = webClient;
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
