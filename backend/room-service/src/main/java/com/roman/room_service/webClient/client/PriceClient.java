package com.roman.room_service.webClient.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class PriceClient {

    private final WebClient webClient;

    public PriceClient(WebClient priceWebClient) {
        this.webClient = priceWebClient;
    }

    public BigDecimal fetchPrice(String roomType) {

        try {
            return webClient.get()
                    .uri("/prices/{roomType}", roomType)
                    .retrieve()
                    .bodyToMono(BigDecimal.class)
                    .timeout(Duration.ofSeconds(2))   // HTTP timeout
                    .retryWhen(Retry.max(1))          // 1 retry
                    .block();
        }
        catch (WebClientResponseException.NotFound e) {
            // 404 → Preis nicht definiert
            return null;
        }
        catch (WebClientResponseException e) {
            // 400, 500, etc.
            return null;
        }
        catch (Exception e) {
            // Timeout, Netzwerkfehler, Connection refused
            return null;
        }
    }
}

