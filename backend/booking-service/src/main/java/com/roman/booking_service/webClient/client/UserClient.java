package com.roman.booking_service.webClient.client;

import com.roman.booking_service.webClient.dto.UserResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import org.springframework.stereotype.Component;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
@Component
public class UserClient {

    private final WebClient webClient;
    public UserClient(@Qualifier("userWebClient") WebClient webClient){
        this.webClient = webClient;
    }
    public UserResponse fetchUserById(Long userId) {

        try {
            return webClient.get()
                    .uri("/userService/{userId}", userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .timeout(Duration.ofSeconds(2))   // HTTP timeout
                    .retryWhen(Retry.max(1))          // 1 retry
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // 404 → Preis nicht definiert
            return null;
        } catch (WebClientResponseException e) {
            // 400, 500, etc.
            return null;
        } catch (Exception e) {
            // Timeout, Netzwerkfehler, Connection refused
            return null;
        }
    }
}
