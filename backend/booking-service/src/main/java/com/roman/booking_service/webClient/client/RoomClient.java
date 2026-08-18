package com.roman.booking_service.webClient.client;
import com.roman.booking_service.webClient.dto.RoomResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import java.math.BigDecimal;
import java.time.Duration;

@Component
public class RoomClient {
    private final WebClient webClient;

    public RoomClient(@Qualifier("roomWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public RoomResponse fetchRoomByNumber(Integer roomNumber) {

        try {
            return webClient.get()
                    .uri("/room/{roomNumber}", roomNumber)
                    .retrieve()
                    .bodyToMono(RoomResponse.class)
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
    public boolean updateRoomAvailability(Integer roomNumber, boolean available) {
        try {
            webClient.patch()
                    .uri("/room/{roomNumber}/availability?available={available}", roomNumber, available)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
