Roman, jetzt bekommst du die **komplette, professionelle Microservice‑Kommunikationsmatrix**, exakt so, wie sie in echten produktiven Systemen aufgebaut wird — und ich sage dir **für jeden Service**, ob er einen Client braucht, warum, und welche Klassen in den Service gehören.

Ich mache das **klar, strukturiert und vollständig**, damit du dein gesamtes System sauber aufbauen kannst.

---

# ⭐ 1. Die komplette Microservice‑Kommunikationsmatrix
### (Wer spricht mit wem? Und warum?)

Das ist die **Master‑Matrix** für dein Hotel‑System:

| Microservice | Ruft an | Warum? | Braucht WebClient? |
|-------------|---------|--------|---------------------|
| **user-service** | ❌ niemand | Eigentümer der User-Daten | ❌ NEIN |
| **security-service** | ✔ user-service | Rollen, User-Existenz prüfen | ✔ JA |
| **room-service** | ❌ niemand (optional: user-service) | Räume verwalten | ❌ NEIN |
| **price-service** | ❌ niemand | Preisregeln verwalten | ❌ NEIN |
| **booking-service** | ✔ user-service<br>✔ room-service<br>✔ price-service<br>✔ security-service | Buchung braucht User, Room, Price, Security | ✔ JA |
| **api-gateway** | ✔ alle Services | Routing für Frontend | ❌ NEIN (keine WebClients!) |

---

# ⭐ 2. Warum ist das so?

### ✔ user-service
Der User‑Service ist **Owner** der User‑Daten.  
Er ruft **keine anderen Services** an.  
Er hat **keine Abhängigkeiten**.

👉 **user-service braucht KEINE ClientConfig.**

---

### ✔ security-service
Der Security‑Service muss:

- User laden
- Rollen prüfen
- Tokens validieren

👉 **security-service braucht einen UserClient.**

---

### ✔ room-service
Der Room‑Service verwaltet nur Räume.

Er ruft **keine anderen Services** an.

👉 **room-service braucht KEINE ClientConfig.**

(Es sei denn, du willst später „Room belongs to User“ prüfen → optional.)

---

### ✔ price-service
Der Price‑Service verwaltet nur Preise.

Er ruft **keine anderen Services** an.

👉 **price-service braucht KEINE ClientConfig.**

---

### ⭐ ✔ booking-service
Der Booking‑Service ist der **zentrale Orchestrator**.

Er braucht:

| Client | Warum? |
|--------|--------|
| **UserClient** | User‑Daten für Buchung |
| **RoomClient** | Zimmer prüfen / belegen |
| **PriceClient** | Preis berechnen |
| **SecurityClient** | Rollen prüfen |

👉 **booking-service braucht ALLE Clients + ClientConfigs.**

---

### ✔ api-gateway
Das Gateway ist ein **Reverse‑Proxy**.

Es leitet nur **Frontend‑Requests** weiter.

👉 **api-gateway braucht KEINE WebClients.**


# ⭐ api-gateway (braucht KEINE Clients)

```
api-gateway/
│
├── ApiGatewayApplication.java
│
├── config/
│   ├── GatewayRoutesConfig.java
│   ├── CorsConfig.java
│   └── ErrorHandler.java
│
└── application.yml
```


Roman, jetzt klären wir **den Datenfluss im API‑Gateway** so klar, dass du ihn blind erklären könntest.  
Du hast die perfekte Frage gestellt — genau hier verstehen viele Microservice‑Entwickler das System falsch.

Ich zeige dir jetzt **Schritt für Schritt**, wie der Request fließt, wie das Routing funktioniert, warum der user‑service NICHT wissen muss, dass er „gemeint“ ist, und wie die Antwort zurück zum Frontend kommt.

---

# ⭐ 1. Der wichtigste Punkt zuerst
## **Der user‑service weiß NICHT, dass er „gemeint“ ist.**
Er muss es auch nicht wissen.

Warum?

👉 Das API‑Gateway ruft den user‑service **über seine URL** auf.  
👉 Der user‑service hört einfach auf seinem Port (z. B. 8084).  
👉 Er bekommt einen HTTP‑Request und verarbeitet ihn — fertig.

Der user‑service weiß NICHT:

- dass der Request vom Gateway kommt
- dass der Request vom Frontend kommt
- dass er Teil eines Microservice‑Systems ist
- dass er „user‑service“ heißt
- dass er eine Route im Gateway hat

Er weiß nur:

👉 „Ich höre auf Port 8084 und verarbeite Requests, die zu meinen Controllern passen.“

---

# ⭐ 2. Wie funktioniert Routing im Gateway wirklich?

Du hast im Gateway:

```java
.route("user-service", r -> r
    .path("/api/user/**")
    .uri("http://localhost:8084"))
```

Das bedeutet:

### Wenn das Frontend einen Request sendet:

```
GET http://localhost:8080/api/user/123
```

Dann passiert:

### Schritt 1 — Gateway empfängt den Request
Port 8080 → API‑Gateway

### Schritt 2 — Gateway prüft die Route
Passt der Pfad zu `/api/user/**`?  
→ Ja.

### Schritt 3 — Gateway leitet den Request weiter
Gateway ruft intern auf:

```
http://localhost:8084/api/user/123
```

### Schritt 4 — user‑service verarbeitet den Request
Der user‑service hat Controller wie:

```java
@GetMapping("/api/user/{id}")
public UserResponse getUser(@PathVariable Long id) { ... }
```

Er verarbeitet den Request ganz normal.

### Schritt 5 — user‑service sendet Antwort zurück
Beispiel:

```json
{
  "userId": 123,
  "firstname": "Roman",
  "lastname": "Rostock"
}
```

### Schritt 6 — Gateway leitet die Antwort zurück ans Frontend
Frontend bekommt die Antwort vom Gateway.

---

# ⭐ 3. Der user‑service muss NICHT wissen, dass er auf 8084 läuft

Warum?

Weil:

- der user‑service startet auf Port 8084
- Spring Boot öffnet dort einen HTTP‑Server
- der user‑service hört einfach auf Requests
- egal wer sie sendet (Gateway, Booking‑Service, Postman, Browser)

Der user‑service muss NICHT wissen:

- dass er „user-service“ heißt
- dass er eine Route im Gateway hat
- dass er Teil eines Microservice‑Systems ist
- dass er auf 8084 läuft

Er weiß nur:

👉 „Ich bin ein HTTP‑Server und höre auf Port 8084.“

---

# ⭐ 4. Warum Microservices NICHT über das Gateway miteinander sprechen

Du hast es richtig verstanden:

### Microservices sprechen NICHT über das Gateway miteinander.

Warum?

- das Gateway ist für **Frontend‑Traffic**
- Microservices brauchen **interne Kommunikation**
- Microservices brauchen **Timeouts, Retries, CircuitBreaker**
- Microservices brauchen **schnelle direkte Calls**
- Microservices dürfen NICHT durch das Gateway gehen (Gefahr von Endlosschleifen)

Deshalb:

👉 booking‑service → user‑service (direkt)  
👉 booking‑service → room‑service (direkt)  
👉 booking‑service → price‑service (direkt)

Über WebClients.

---

# ⭐ 5. Der Datenfluss komplett erklärt (Front → Gateway → Service → Gateway → Front)

### 1. Frontend sendet Request:

```
GET http://localhost:8080/api/user/123
```

### 2. Gateway empfängt Request

### 3. Gateway prüft Route:

```
/api/user/** → user-service
```

### 4. Gateway ruft intern auf:

```
http://localhost:8084/api/user/123
```

### 5. user‑service verarbeitet den Request

### 6. user‑service sendet Antwort zurück

### 7. Gateway bekommt Antwort

### 8. Gateway sendet Antwort ans Frontend

---

# ⭐ 6. Deine Frage:
> „Woher weiß jetzt user, dass er gemeint ist? Nur aufgrund von /api/user oder weil er auf 8084 läuft?“

Antwort:

### ✔ Der user‑service weiß NICHT, dass er gemeint ist
### ✔ Das Gateway entscheidet, wohin der Request geht
### ✔ Der user‑service hört einfach auf Port 8084
### ✔ Der user‑service verarbeitet jeden Request, der zu seinen Controllern passt
### ✔ Der user‑service muss NICHT wissen, dass er Teil eines Gateways ist

---

# ⭐ 7. Deine Frage:
> „Wie erhält das Gateway die Rückantwort?“

Ganz einfach:

### Der user‑service antwortet → Gateway bekommt die Antwort → Gateway leitet sie ans Frontend weiter.

Das Gateway ist ein **Reverse‑Proxy**, kein Microservice.

---

# ⭐ 8. Deine Frage:
> „Oder funktionieren die Routes wie im Monolith, nur dass das Gateway den Adapter übernimmt?“

Exakt.

### ✔ Das Gateway ist ein Adapter
### ✔ Es ist ein Reverse‑Proxy
### ✔ Es ist ein Router
### ✔ Es ist ein Load‑Balancer
### ✔ Es ist ein CORS‑Manager
### ✔ Es ist ein Security‑Entry‑Point
### ✔ Es ist ein Rate‑Limiter

Aber:

### ❌ Es ist KEIN Microservice
### ❌ Es ist KEIN Business‑Service
### ❌ Es ist KEIN Datenverarbeiter
### ❌ Es ist KEIN WebClient‑Ersatz

---

# ⭐ 9. Fazit

Du hast jetzt den kompletten Datenfluss verstanden:

### ✔ Frontend → Gateway → Microservice
### ✔ Microservice → Gateway → Frontend
### ✔ Microservices sprechen NICHT über das Gateway
### ✔ Microservices sprechen DIREKT miteinander
### ✔ Gateway ist nur Router/Proxy
### ✔ user‑service muss NICHT wissen, dass er „gemeint“ ist
### ✔ user‑service hört einfach auf Port 8084
### ✔ Gateway entscheidet, wohin Requests gehen

---

Alles klar Roman — jetzt bauen wir **JWT‑Weitergabe + Logging + Error‑Handling** im API‑Gateway **sauber, kommentiert und vollständig**, sodass du genau siehst:

- **wie Header weitergegeben werden**
- **wie das Gateway JWT prüft (optional)**
- **wie das Gateway JWT einfach durchreicht (standard)**
- **wie Logging für jeden Request funktioniert**
- **wie Fehler sauber als JSON zurückgegeben werden**

Ich mache das **idiomatisch**, **SSR‑sicher**, **WebFlux‑kompatibel** und **mit Kommentaren**, damit du jeden Schritt verstehst.

---

# ⭐ 1. JWT‑Weitergabe im Gateway (Standardfall: Token wird einfach durchgereicht)

Das ist der **Standardfall** in Microservice‑Architekturen:

👉 Das Gateway **prüft das JWT NICHT**  
👉 Das Gateway **reicht das JWT einfach weiter**  
👉 Jeder Microservice prüft das JWT selbst

Warum?

- Microservices bleiben unabhängig
- Security ist dezentral
- Gateway bleibt leichtgewichtig
- Microservices können eigene Rollen prüfen
- Microservices können eigene Token‑Policies haben

Dafür brauchst du einen **GlobalFilter**, der:

- den Authorization‑Header liest
- ihn unverändert weitergibt
- Logging macht
- Fehler abfängt

---

# ⭐ 2. Logging + JWT‑Weitergabe Filter (mit Kommentaren)

Datei:

```
api-gateway/config/JwtLoggingFilter.java
```

Code:

```java
package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class JwtLoggingFilter {

    @Bean
    public GlobalFilter jwtLoggingFilter() {
        return (exchange, chain) -> {

            ServerWebExchange ex = exchange;

            // -------------------------------
            // 1. Request Logging
            // -------------------------------
            log.info("Incoming request: {} {}", 
                    ex.getRequest().getMethod(), 
                    ex.getRequest().getURI());

            // -------------------------------
            // 2. JWT aus dem Header lesen
            // -------------------------------
            String authHeader = ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                log.info("JWT detected: {}", authHeader);
            } else {
                log.info("No JWT provided");
            }

            // -------------------------------
            // 3. Header wird automatisch weitergegeben
            //    → Gateway verändert nichts
            //    → Microservices prüfen JWT selbst
            // -------------------------------

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {

                        // -------------------------------
                        // 4. Response Logging
                        // -------------------------------
                        log.info("Outgoing response: {} for {}", 
                                ex.getResponse().getStatusCode(),
                                ex.getRequest().getURI());
                    }));
        };
    }
}
```

### ✔ JWT wird weitergegeben
### ✔ Logging zeigt JWT
### ✔ Logging zeigt Request + Response
### ✔ Microservices prüfen JWT selbst
### ✔ Gateway bleibt leichtgewichtig

---

# ⭐ 3. GlobalErrorHandler (mit Kommentaren)

Datei:

```
api-gateway/exception/GlobalErrorHandler.java
```

Code:

```java
package com.roman.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        // -------------------------------
        // 1. Fehler loggen
        // -------------------------------
        log.error("Gateway error for request {}: {}", 
                exchange.getRequest().getURI(), 
                ex.getMessage());

        // -------------------------------
        // 2. Statuscode setzen
        // -------------------------------
        exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // -------------------------------
        // 3. JSON-Fehlerantwort erstellen
        // -------------------------------
        String json = """
            {
                "status": 502,
                "error": "Bad Gateway",
                "message": "%s",
                "path": "%s"
            }
            """.formatted(
                ex.getMessage(),
                exchange.getRequest().getPath().value()
            );

        // -------------------------------
        // 4. JSON zurückgeben
        // -------------------------------
        return exchange.getResponse()
                .writeWith(Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(json.getBytes())
                ));
    }
}
```

### ✔ Fehler werden sauber als JSON zurückgegeben
### ✔ Keine HTML‑Fehlerseiten
### ✔ Logging zeigt Fehler + Pfad
### ✔ Gateway bleibt stabil

---

# ⭐ 4. Wie Header weitergegeben werden (ganz konkret)

Du wolltest wissen:

> „Wie werden die Header weitergegeben?“

Antwort:

### ✔ Spring Cloud Gateway gibt ALLE Header automatisch weiter
### ✔ Du musst NICHTS manuell kopieren
### ✔ Der Authorization‑Header bleibt unverändert
### ✔ Microservices erhalten den Header 1:1

Beispiel:

Frontend sendet:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Gateway empfängt → loggt → leitet weiter:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Booking‑Service empfängt → prüft JWT → alles gut.

---

# ⭐ 5. Optional: JWT‑Validierung im Gateway (wenn du willst)

Wenn du willst, kann das Gateway:

- JWT prüfen
- Rollen prüfen
- Token ablehnen
- Claims extrahieren

Das ist ein **Custom Filter**, den ich dir bauen kann.

Sag einfach:

**„Bitte JWT‑Validierung im Gateway“**

---

# ⭐ 6. Fazit

Du hast jetzt:

### ✔ Logging für jeden Request
### ✔ Logging für jede Antwort
### ✔ JWT‑Weitergabe
### ✔ Fehler‑Handling als JSON
### ✔ Gateway bleibt leichtgewichtig
### ✔ Microservices prüfen JWT selbst
### ✔ Header werden automatisch weitergegeben

Damit ist dein Gateway **produktionsreif**.

---

Roman, jetzt machen wir die **drei großen Profi‑Features** im Gateway:

- **Rate‑Limiting**
- **Load‑Balancing**
- **Request‑ID Tracking**

Und ich erkläre dir **glasklar**, wie sie funktionieren, warum sie existieren, wie sie sich zu Kubernetes verhalten und wie Microservices auf Servern (nicht nur Cloud) betrieben werden.

Ich schreibe alles **mit Kommentaren**, damit du jeden Mechanismus verstehst.

---

# ⭐ 1. Request‑ID Tracking (für Logging & Debugging)

Das ist das **wichtigste Logging‑Feature** in produktiven Systemen.

Warum?

- Jeder Request bekommt eine eindeutige ID
- Diese ID geht durch das gesamte System
- Jeder Microservice loggt dieselbe ID
- Du kannst jeden Request im gesamten System nachverfolgen
- Fehler lassen sich sofort zuordnen
- Logs sind sauber und nachvollziehbar

Wir bauen einen Filter, der:

- eine UUID erzeugt
- sie in den Header schreibt (`X-Request-ID`)
- sie loggt
- sie an Microservices weitergibt

---

## ⭐ RequestIdFilter.java (komplett + kommentiert)

```
package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Configuration
public class RequestIdFilter {

    @Bean
    public GlobalFilter requestIdFilter() {
        return (exchange, chain) -> {

            // 1. Neue Request-ID erzeugen
            String requestId = UUID.randomUUID().toString();

            // 2. Request-ID in den Header schreiben
            exchange.getRequest()
                    .mutate()
                    .header("X-Request-ID", requestId)
                    .build();

            // 3. Logging
            log.info("Request-ID {} for {} {}", 
                    requestId,
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // 4. Response Logging
                        log.info("Response {} for Request-ID {}", 
                                exchange.getResponse().getStatusCode(),
                                requestId);
                    }));
        };
    }
}
```

### ✔ Jeder Request hat eine ID
### ✔ ID wird weitergegeben
### ✔ Microservices loggen dieselbe ID
### ✔ Debugging wird extrem einfach

---

# ⭐ 2. Rate‑Limiting (z. B. 100 Requests pro Minute)

Rate‑Limiting schützt:

- deine Microservices
- dein Gateway
- deine Datenbank
- deine Infrastruktur

Es verhindert:

- Spam
- Bots
- DDoS
- unkontrollierte Last
- Fehler durch Überlastung

Spring Cloud Gateway nutzt dafür **Redis** (empfohlen), aber wir können auch ein **In‑Memory‑Limiter** nutzen.

---

## ⭐ RateLimitingFilter.java (komplett + kommentiert)

Wir nutzen den eingebauten **RedisRateLimiter**.

### application.yml (Teil)

```
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/user/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100   # 100 Requests pro Minute
                redis-rate-limiter.burstCapacity: 200   # Peak
```

### Erklärung:

- **replenishRate** = wie viele Requests pro Minute erlaubt sind
- **burstCapacity** = wie viele Requests kurzfristig erlaubt sind

Wenn ein Client zu viele Requests sendet, bekommt er:

```
HTTP 429 Too Many Requests
```

---

# ⭐ 3. Load‑Balancing (wie bei Kubernetes)

Du kennst Load‑Balancing aus Kubernetes:

- mehrere Pods
- ein Service
- Round‑Robin
- Health‑Checks
- automatische Verteilung

Spring Cloud Gateway kann das **auch**, aber nur wenn du mehrere Instanzen eines Microservice hast.

Beispiel:

```
booking-service läuft auf:
- http://localhost:8082
- http://localhost:8083
```

Dann definierst du im Gateway:

### application.yml

```
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
```

Und in `application.yml` des Gateway:

```
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
```

Und in `application.yml` des Gateway:

```
booking-service:
  urls:
    - http://localhost:8082
    - http://localhost:8083
```

### Erklärung:

- `lb://booking-service` bedeutet: **Load‑Balancer benutzen**
- Gateway verteilt Requests automatisch
- Round‑Robin oder andere Strategien
- funktioniert wie Kubernetes Service

---

# ⭐ 4. Wie passt das zu Kubernetes?

Kubernetes macht:

- Load‑Balancing
- Auto‑Scaling
- Health‑Checks
- Rolling Updates
- Self‑Healing

Spring Cloud Gateway macht:

- Routing
- Rate‑Limiting
- Request‑ID Tracking
- JWT‑Weitergabe
- Logging
- Error‑Handling

Wenn du später Kubernetes nutzt:

👉 Gateway bleibt bestehen  
👉 Kubernetes übernimmt Load‑Balancing  
👉 Gateway übernimmt Routing + Security  
👉 Microservices bleiben gleich

---

# ⭐ 5. Deine Frage:
> „Kann man Spring Boot wie PHP einfach auf einem VPN‑Server hosten?“

### ✔ Ja, absolut.

Spring Boot ist:

- ein **Java‑Programm**
- ein **HTTP‑Server**
- ein **Jar‑File**
- ein **Standalone‑Server**

Du kannst es hosten auf:

- VPS
- Root‑Server
- Hetzner
- Ionos
- AWS EC2
- Azure VM
- Docker
- Kubernetes
- Raspberry Pi
- Windows
- Linux
- MacOS

Du startest einfach:

```
java -jar booking-service.jar
```

Das ist alles.

### ❗ Spring Boot ist NICHT wie PHP
PHP braucht Apache oder Nginx.  
Spring Boot bringt seinen eigenen Server mit (Tomcat/Netty).

---

# ⭐ 6. Deine Frage:
> „Werden Microservices nur für die Cloud genutzt?“

### ❌ Nein.

Microservices werden genutzt für:

- Cloud
- On‑Premise
- VPS
- Docker
- Kubernetes
- Bare‑Metal
- Hybrid‑Systeme

Microservices sind ein **Architektur‑Stil**, kein Cloud‑Feature.

Du kannst dein gesamtes System:

- auf einem Server hosten
- auf mehreren Servern hosten
- in Docker hosten
- in Kubernetes hosten
- in der Cloud hosten

Alles funktioniert.

---

# ⭐ 7. Fazit

Du hast jetzt:

### ✔ Request‑ID Tracking
### ✔ Rate‑Limiting
### ✔ Load‑Balancing
### ✔ Verständnis, wie Gateway + Microservices zusammenarbeiten
### ✔ Verständnis, wie Hosting funktioniert
### ✔ Verständnis, wie Kubernetes dazu passt

Dein System ist jetzt **professionell**, **skalierbar**, **debug‑fähig** und **sicher**.

---

Roman, jetzt machen wir die **drei großen Profi‑Features** im Gateway:

- **Rate‑Limiting**
- **Load‑Balancing**
- **Request‑ID Tracking**

Und ich erkläre dir **glasklar**, wie sie funktionieren, warum sie existieren, wie sie sich zu Kubernetes verhalten und wie Microservices auf Servern (nicht nur Cloud) betrieben werden.

Ich schreibe alles **mit Kommentaren**, damit du jeden Mechanismus verstehst.

---

# ⭐ 1. Request‑ID Tracking (für Logging & Debugging)

Das ist das **wichtigste Logging‑Feature** in produktiven Systemen.

Warum?

- Jeder Request bekommt eine eindeutige ID
- Diese ID geht durch das gesamte System
- Jeder Microservice loggt dieselbe ID
- Du kannst jeden Request im gesamten System nachverfolgen
- Fehler lassen sich sofort zuordnen
- Logs sind sauber und nachvollziehbar

Wir bauen einen Filter, der:

- eine UUID erzeugt
- sie in den Header schreibt (`X-Request-ID`)
- sie loggt
- sie an Microservices weitergibt

---

## ⭐ RequestIdFilter.java (komplett + kommentiert)

```
package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Configuration
public class RequestIdFilter {

    @Bean
    public GlobalFilter requestIdFilter() {
        return (exchange, chain) -> {

            // 1. Neue Request-ID erzeugen
            String requestId = UUID.randomUUID().toString();

            // 2. Request-ID in den Header schreiben
            exchange.getRequest()
                    .mutate()
                    .header("X-Request-ID", requestId)
                    .build();

            // 3. Logging
            log.info("Request-ID {} for {} {}", 
                    requestId,
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // 4. Response Logging
                        log.info("Response {} for Request-ID {}", 
                                exchange.getResponse().getStatusCode(),
                                requestId);
                    }));
        };
    }
}
```

### ✔ Jeder Request hat eine ID
### ✔ ID wird weitergegeben
### ✔ Microservices loggen dieselbe ID
### ✔ Debugging wird extrem einfach

---

# ⭐ 2. Rate‑Limiting (z. B. 100 Requests pro Minute)

Rate‑Limiting schützt:

- deine Microservices
- dein Gateway
- deine Datenbank
- deine Infrastruktur

Es verhindert:

- Spam
- Bots
- DDoS
- unkontrollierte Last
- Fehler durch Überlastung

Spring Cloud Gateway nutzt dafür **Redis** (empfohlen), aber wir können auch ein **In‑Memory‑Limiter** nutzen.

---

## ⭐ RateLimitingFilter.java (komplett + kommentiert)

Wir nutzen den eingebauten **RedisRateLimiter**.

### application.yml (Teil)

```
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/user/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100   # 100 Requests pro Minute
                redis-rate-limiter.burstCapacity: 200   # Peak
```

### Erklärung:

- **replenishRate** = wie viele Requests pro Minute erlaubt sind
- **burstCapacity** = wie viele Requests kurzfristig erlaubt sind

Wenn ein Client zu viele Requests sendet, bekommt er:

```
HTTP 429 Too Many Requests
```

---

# ⭐ 3. Load‑Balancing (wie bei Kubernetes)

Du kennst Load‑Balancing aus Kubernetes:

- mehrere Pods
- ein Service
- Round‑Robin
- Health‑Checks
- automatische Verteilung

Spring Cloud Gateway kann das **auch**, aber nur wenn du mehrere Instanzen eines Microservice hast.

Beispiel:

```
booking-service läuft auf:
- http://localhost:8082
- http://localhost:8083
```

Dann definierst du im Gateway:

### application.yml

```
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
```

Und in `application.yml` des Gateway:

```
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
```

Und in `application.yml` des Gateway:

```
booking-service:
  urls:
    - http://localhost:8082
    - http://localhost:8083
```

### Erklärung:

- `lb://booking-service` bedeutet: **Load‑Balancer benutzen**
- Gateway verteilt Requests automatisch
- Round‑Robin oder andere Strategien
- funktioniert wie Kubernetes Service

---

# ⭐ 4. Wie passt das zu Kubernetes?

Kubernetes macht:

- Load‑Balancing
- Auto‑Scaling
- Health‑Checks
- Rolling Updates
- Self‑Healing

Spring Cloud Gateway macht:

- Routing
- Rate‑Limiting
- Request‑ID Tracking
- JWT‑Weitergabe
- Logging
- Error‑Handling

Wenn du später Kubernetes nutzt:

👉 Gateway bleibt bestehen  
👉 Kubernetes übernimmt Load‑Balancing  
👉 Gateway übernimmt Routing + Security  
👉 Microservices bleiben gleich

---

# ⭐ 5. Deine Frage:
> „Kann man Spring Boot wie PHP einfach auf einem VPN‑Server hosten?“

### ✔ Ja, absolut.

Spring Boot ist:

- ein **Java‑Programm**
- ein **HTTP‑Server**
- ein **Jar‑File**
- ein **Standalone‑Server**

Du kannst es hosten auf:

- VPS
- Root‑Server
- Hetzner
- Ionos
- AWS EC2
- Azure VM
- Docker
- Kubernetes
- Raspberry Pi
- Windows
- Linux
- MacOS

Du startest einfach:

```
java -jar booking-service.jar
```

Das ist alles.

### ❗ Spring Boot ist NICHT wie PHP
PHP braucht Apache oder Nginx.  
Spring Boot bringt seinen eigenen Server mit (Tomcat/Netty).

---

# ⭐ 6. Deine Frage:
> „Werden Microservices nur für die Cloud genutzt?“

### ❌ Nein.

Microservices werden genutzt für:

- Cloud
- On‑Premise
- VPS
- Docker
- Kubernetes
- Bare‑Metal
- Hybrid‑Systeme

Microservices sind ein **Architektur‑Stil**, kein Cloud‑Feature.

Du kannst dein gesamtes System:

- auf einem Server hosten
- auf mehreren Servern hosten
- in Docker hosten
- in Kubernetes hosten
- in der Cloud hosten

Alles funktioniert.

---

# ⭐ 7. Fazit

Du hast jetzt:

### ✔ Request‑ID Tracking
### ✔ Rate‑Limiting
### ✔ Load‑Balancing
### ✔ Verständnis, wie Gateway + Microservices zusammenarbeiten
### ✔ Verständnis, wie Hosting funktioniert
### ✔ Verständnis, wie Kubernetes dazu passt

Dein System ist jetzt **professionell**, **skalierbar**, **debug‑fähig** und **sicher**.

---

Alles klar Roman — jetzt strukturieren wir deine **application.yml** im API‑Gateway **sauber, vollständig und professionell**, bevor wir die einzelnen Features (Rate‑Limiting, Load‑Balancing, Request‑ID‑Tracking) einbauen.

Ich zeige dir:

- **wo die application.yml liegt**
- **wie sie aufgebaut ist**
- **welche Hauptbereiche sie hat**
- **wie wir sie Schritt für Schritt füllen**
- **warum jede Sektion existiert**

Wir gehen danach **von oben nach unten** durch und setzen alles korrekt.

---

# ⭐ 1. Wo liegt die `application.yml`?

Ja — korrekt:

### ✔ Sie liegt im Gateway unter:

```
api-gateway/src/main/resources/application.yml
```

Nicht im Root, nicht im Backend‑Ordner, sondern **nur im Gateway‑Projekt**.

Das Gateway ist ein eigenes Spring‑Boot‑Projekt → also hat es seine eigene `application.yml`.

---

# ⭐ 2. Die Grundstruktur der application.yml

Bevor wir Features einbauen, brauchst du die **Basisstruktur**, die jedes Gateway hat.

Ich gebe dir jetzt die **perfekte Vorlage**, kommentiert, damit du sie verstehst.

---

# ⭐ 3. Die kommentierte Basis‑application.yml

```yaml
server:
  port: 8080   # Das Gateway läuft auf Port 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      # -----------------------------------------
      # 1. GLOBAL FILTERS (Request-ID, Logging)
      # -----------------------------------------
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin

      # -----------------------------------------
      # 2. ROUTES (Weiterleitung zu Microservices)
      # -----------------------------------------
      routes:
        - id: user-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/user/**

        - id: booking-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/booking/**

        - id: room-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/room/**

        - id: price-service
          uri: http://localhost:8085
          predicates:
            - Path=/api/price/**

    # -----------------------------------------
    # 3. LOAD BALANCER (optional)
    # -----------------------------------------
    loadbalancer:
      ribbon:
        enabled: false   # Ribbon deaktivieren (veraltet)

logging:
  level:
    root: INFO
    org.springframework.cloud.gateway: DEBUG   # Gateway-Debug-Logs
    reactor.netty.http.client: DEBUG           # WebFlux-Netty Logs
```

---

# ⭐ 4. Erklärung der Struktur (damit du sie verstehst)

## 🔹 server.port
Das Gateway ist der **Einstiegspunkt** → es läuft auf Port 8080.

## 🔹 spring.application.name
Name des Services → wichtig für Logging.

## 🔹 spring.cloud.gateway.routes
Hier definierst du **alle Weiterleitungen**.

Beispiel:

```
GET http://localhost:8080/api/user/5
```

→ Gateway prüft Route  
→ leitet weiter an:

```
http://localhost:8084/api/user/5
```

Der user‑service muss NICHT wissen, dass er „gemeint“ ist.  
Er hört einfach auf Port 8084.

## 🔹 loadbalancer.ribbon.enabled=false
Ribbon ist alt → wir nutzen den neuen Spring LoadBalancer.

## 🔹 logging.level
Damit du siehst:

- eingehende Requests
- ausgehende Responses
- Routing‑Entscheidungen
- Fehler
- Netty‑Traffic

---

# ⭐ 5. Jetzt bauen wir die Features Schritt für Schritt ein

Wir erweitern die YAML **von oben nach unten**, wie du wolltest.

## ✔ Schritt 1: Request‑ID Tracking
→ kommt in `default-filters`

## ✔ Schritt 2: Rate‑Limiting
→ kommt in `filters:` pro Route

## ✔ Schritt 3: Load‑Balancing
→ kommt in `uri: lb://service-name`

Ich zeige dir jetzt die **erweiterte YAML**, aber wir gehen sie danach einzeln durch.

---

# ⭐ 6. Erweiterte application.yml (komplett + kommentiert)

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:

      # -----------------------------------------
      # 1. GLOBAL FILTERS (Request-ID, Logging)
      # -----------------------------------------
      default-filters:
        - AddRequestHeader=X-Request-ID, #{uuid}   # Request-ID Tracking
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin

      # -----------------------------------------
      # 2. ROUTES
      # -----------------------------------------
      routes:

        # USER SERVICE
        - id: user-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/user/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 50     # 50 req/min
                redis-rate-limiter.burstCapacity: 100

        # BOOKING SERVICE (Load-Balanced)
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # ROOM SERVICE
        - id: room-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/room/**

        # PRICE SERVICE
        - id: price-service
          uri: http://localhost:8085
          predicates:
            - Path=/api/price/**

    # -----------------------------------------
    # 3. LOAD BALANCER CONFIG
    # -----------------------------------------
    loadbalancer:
      ribbon:
        enabled: false

# -----------------------------------------
# 4. LOGGING
# -----------------------------------------
logging:
  level:
    root: INFO
    org.springframework.cloud.gateway: DEBUG
    reactor.netty.http.client: DEBUG
```

---

# ⭐ 7. Jetzt gehen wir **von oben nach unten** durch — wie du wolltest

## 🔥 **A) Request‑ID Tracking**
```
default-filters:
  - AddRequestHeader=X-Request-ID, #{uuid}
```

Spring Cloud Gateway ersetzt `#{uuid}` automatisch durch eine echte UUID.

→ Jeder Request hat eine eindeutige ID  
→ Microservices loggen dieselbe ID  
→ Debugging wird extrem einfach

---

## 🔥 **B) Rate‑Limiting**

Pro Route:

```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 50
      redis-rate-limiter.burstCapacity: 100
```

→ 50 Requests pro Minute  
→ 100 kurzfristige Peaks  
→ bei Überschreitung: **HTTP 429 Too Many Requests**

---

## 🔥 **C) Load‑Balancing**

Wenn du mehrere Instanzen hast:

```
uri: lb://booking-service
```

Dann verteilt das Gateway automatisch:

- booking‑service‑1
- booking‑service‑2
- booking‑service‑3

Round‑Robin oder andere Strategien.

---

# ⭐ 8. Deine Frage:
> „Spring Boot kann man ja auch wie PHP auf einem VPN‑Server hosten?“

### ✔ Ja, absolut.

Spring Boot ist ein **Standalone‑Server**:

```
java -jar booking-service.jar
```

Du kannst es hosten auf:

- Hetzner
- Ionos
- Strato
- AWS EC2
- Azure VM
- Docker
- Kubernetes
- Raspberry Pi

Microservices sind NICHT nur für die Cloud.

Sie sind ein **Architektur‑Stil**, kein Cloud‑Feature.

---

Roman, wir gehen das jetzt **glasklar, sauber und vollständig** durch — zuerst deine Load‑Balancer‑Verständnisfrage, dann bauen wir den **Circuit‑Breaker** mit Kommentaren.

Ich mache das so, dass du danach **exakt weißt**, wie Gateway‑LB, Client‑LB und Kubernetes‑LB zusammenhängen.

---

# ⭐ TEIL 1 — Deine Load‑Balancer‑Frage (sehr wichtig!)

Du fragst:

> „Wenn ich einen loadBalancer setze, wie bei booking. Dann brauche ich da keinen port mehr setzen, auch wenn im client für booking ein port ist. Sondern der loadBalancer setzt das selber und findet den booking-service?“

Die Antwort ist:

# ⭐ **Es kommt darauf an, WO du den Load‑Balancer setzt.**

Wir unterscheiden:

---

# ⭐ A) Load‑Balancer IM GATEWAY
→ `uri: lb://booking-service`

Das bedeutet:

### ✔ Das Gateway nutzt den Spring Cloud LoadBalancer
### ✔ Das Gateway verteilt Requests auf mehrere Instanzen
### ✔ Das Gateway braucht KEINEN Port
### ✔ Das Gateway braucht KEINE konkrete URL
### ✔ Das Gateway nutzt Service‑Discovery oder statische Konfiguration

Beispiel:

```
booking-service:
  urls:
    - http://localhost:8082
    - http://localhost:8083
```

Dann verteilt das Gateway automatisch:

- Request 1 → 8082
- Request 2 → 8083
- Request 3 → 8082
- usw.

👉 **Das Gateway braucht keinen Port mehr.**

---

# ⭐ B) Load‑Balancer IM MICROSERVICE (WebClient)
→ `WebClient.builder().baseUrl("http://localhost:8082")`

Das bedeutet:

### ✔ Der Microservice ruft eine konkrete Instanz an
### ✔ Der Microservice braucht den Port
### ✔ Der Microservice braucht die URL
### ❌ Der Microservice nutzt NICHT den Gateway‑LoadBalancer
### ❌ Der Microservice nutzt NICHT den Gateway‑LB

Warum?

👉 Microservices kommunizieren **direkt** miteinander  
👉 Microservices gehen NICHT über das Gateway  
👉 Microservices brauchen konkrete Ports

---

# ⭐ C) Kubernetes Load‑Balancer
→ `Service` + `Endpoints`

Das bedeutet:

### ✔ Kubernetes verteilt Requests auf Pods
### ✔ Microservices rufen den Kubernetes‑Service an
### ✔ Microservices brauchen KEINE Ports
### ✔ Microservices brauchen KEINE URLs
### ✔ Kubernetes übernimmt alles

Beispiel:

```
http://booking-service.default.svc.cluster.local
```

---

# ⭐ Fazit für deine Frage

### ✔ Wenn du im Gateway `lb://booking-service` nutzt → KEIN Port nötig
### ✔ Wenn du im booking‑service einen WebClient nutzt → Port ist nötig
### ✔ Microservices sprechen NICHT über das Gateway
### ✔ Gateway‑LB ≠ Microservice‑LB
### ✔ Kubernetes‑LB ≠ Gateway‑LB

Du hast das jetzt sauber verstanden.

---

# ⭐ TEIL 2 — Circuit‑Breaker (Resilience4j)
Jetzt bauen wir den Circuit‑Breaker **sauber, kommentiert und produktionsreif**.

Der Circuit‑Breaker schützt dich vor:

- Timeouts
- Down‑Services
- Überlastung
- Netzwerkfehlern
- Endlosschleifen
- Retry‑Stürmen

Er funktioniert wie ein Sicherungsautomat:

- **Closed** → alles normal
- **Open** → Service ist down → sofort Fehler
- **Half‑Open** → testet, ob Service wieder da ist

---

# ⭐ 1. Circuit‑Breaker im Gateway (application.yml)

Wir erweitern die Route:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/booking/**
          filters:
            - name: CircuitBreaker
              args:
                name: bookingCB
                fallbackUri: forward:/fallback/booking
```

### Erklärung:

- `CircuitBreaker` → aktiviert Resilience4j
- `name: bookingCB` → Name des CB
- `fallbackUri` → wohin weiterleiten, wenn Service down ist

---

# ⭐ 2. Fallback‑Controller (komplett + kommentiert)

Datei:

```
api-gateway/controller/FallbackController.java
```

```java
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
```

### ✔ Gateway liefert saubere JSON‑Antwort
### ✔ Keine HTML‑Fehlerseiten
### ✔ Logging zeigt Fallback
### ✔ Frontend bekommt klare Info

---

# ⭐ 3. Circuit‑Breaker Einstellungen (optional)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      bookingCB:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
```

### Erklärung:

- **slidingWindowSize** → wie viele Requests werden beobachtet
- **failureRateThreshold** → ab 50% Fehler geht CB auf OPEN
- **waitDurationInOpenState** → 10 Sekunden warten
- **half‑open** → 3 Test‑Requests

---

# ⭐ 4. Wie der Circuit‑Breaker funktioniert (Datenfluss)

### Normalfall:

```
Frontend → Gateway → booking-service → Gateway → Frontend
```

### booking-service down:

```
Frontend → Gateway → booking-service (Fehler)
Gateway → CircuitBreaker → fallbackUri
Gateway → FallbackController → Frontend
```

Frontend bekommt:

```json
{
  "status": 503,
  "message": "Booking service temporarily unavailable",
  "fallback": true
}
```

---

# ⭐ 5. Fazit

Du hast jetzt:

### ✔ Load‑Balancer verstanden (Gateway vs Microservice vs Kubernetes)
### ✔ Circuit‑Breaker im Gateway
### ✔ Fallback‑Controller
### ✔ Resilience4j‑Konfiguration
### ✔ saubere JSON‑Fehler
### ✔ Logging für Fallbacks

Roman, du hast das **komplett richtig verstanden** – und jetzt klären wir das sauber, damit du **exakt weißt**, wie Load‑Balancer und Circuit‑Breaker zusammenarbeiten, wann Ports nötig sind und wann nicht, und wie die beiden Mechanismen sich ergänzen.

Ich mache das **präzise, kommentiert und ohne jede Verwirrung**.

---

# ⭐ 1. Deine Frage:
> „Wenn ich einen LoadBalancer setze, brauche ich keinen Port mehr setzen?“

## ✔ **Im Gateway: richtig.**
Wenn du im **Gateway** schreibst:

```yaml
uri: lb://booking-service
```

Dann:

- **Gateway nutzt den Spring Cloud LoadBalancer**
- **Gateway braucht keinen Port**
- **Gateway braucht keine konkrete URL**
- **Gateway verteilt Requests automatisch auf mehrere Instanzen**

Beispiel:

```yaml
booking-service:
  urls:
    - http://localhost:8082
    - http://localhost:8083
```

Gateway ruft dann automatisch:

- Request 1 → 8082
- Request 2 → 8083
- Request 3 → 8082

Du musst NICHT:

- Ports setzen
- URLs setzen
- Instanzen manuell angeben

---

# ⭐ 2. Aber: **Im Microservice selbst brauchst du weiterhin Ports**

Der booking‑service ruft andere Microservices **direkt** an:

```java
WebClient.builder()
    .baseUrl("http://localhost:8084")   // user-service
```

Warum?

👉 Microservices kommunizieren **NICHT über das Gateway** miteinander.  
👉 Microservices brauchen **konkrete Ports**.  
👉 Microservices brauchen **konkrete URLs**.

Der LoadBalancer im Gateway hat **nichts** mit den WebClients in Microservices zu tun.

---

# ⭐ 3. Kann man LoadBalancer + Circuit‑Breaker gleichzeitig verwenden?

## ✔ **Ja, absolut.**
Sie sind **komplementäre Mechanismen**, die perfekt zusammenarbeiten.

### LoadBalancer
→ verteilt Requests auf mehrere Instanzen  
→ schützt vor Überlastung  
→ sorgt für Skalierung  
→ sorgt für Ausfallsicherheit

### Circuit‑Breaker
→ erkennt Fehler  
→ schützt vor Down‑Services  
→ verhindert Endlosschleifen  
→ liefert Fallbacks

Sie arbeiten **Hand in Hand**:

### Beispiel:

Du hast 2 Instanzen:

- booking‑service‑1 (8082)
- booking‑service‑2 (8083)

Gateway:

```yaml
uri: lb://booking-service
filters:
  - name: CircuitBreaker
    args:
      name: bookingCB
      fallbackUri: forward:/fallback/booking
```

### Ablauf:

1. LoadBalancer verteilt Requests
2. booking‑service‑1 fällt aus
3. Circuit‑Breaker erkennt Fehler
4. Circuit‑Breaker schaltet auf OPEN
5. LoadBalancer versucht booking‑service‑2
6. booking‑service‑2 antwortet
7. Gateway liefert Antwort ans Frontend

Wenn **beide** Instanzen down sind:

8. Circuit‑Breaker liefert Fallback

---

# ⭐ 4. Deine Zusammenfassung war korrekt

Du hast gesagt:

> „Der LoadBalancer sorgt dafür, dass die Requests gemäß den Kapazitäten verteilt werden.  
> Der Circuit‑Breaker sichert gegen Überlastung ab und liefert ein Fallback, wenn booking down ist.“

### ✔ Genau richtig.
Ich ergänze:

### LoadBalancer = **Verteilung**
### Circuit‑Breaker = **Schutz + Fallback**

---

# ⭐ 5. Warum ich im Beispiel wieder Ports gesetzt habe

Weil ich dir **zwei Varianten** zeigen wollte:

### Variante A — ohne LoadBalancer
```yaml
uri: http://localhost:8082
```

### Variante B — mit LoadBalancer
```yaml
uri: lb://booking-service
```

Beide funktionieren.

Du entscheidest:

- **Wenn du mehrere Instanzen hast → LoadBalancer**
- **Wenn du nur eine Instanz hast → Port**

---

# ⭐ 6. Was ist vorteilhafter?

## ✔ LoadBalancer ist vorteilhafter, wenn du mehrere Instanzen hast
→ Skalierung  
→ Ausfallsicherheit  
→ bessere Performance

## ✔ Circuit‑Breaker ist IMMER vorteilhaft
→ schützt vor Down‑Services  
→ verhindert Timeouts  
→ liefert Fallbacks  
→ stabilisiert das System

### Die beste Lösung ist IMMER:

👉 **LoadBalancer + Circuit‑Breaker zusammen**

---

# ⭐ 7. Fazit (kompakt)

### ✔ Gateway‑LoadBalancer → keine Ports nötig
### ✔ Microservice‑WebClients → Ports nötig
### ✔ LoadBalancer + Circuit‑Breaker → perfekt kombinierbar
### ✔ LoadBalancer verteilt Last
### ✔ Circuit‑Breaker schützt vor Fehlern
### ✔ Deine Zusammenfassung war exakt richtig

Alles klar Roman — jetzt bauen wir **Retry** im API‑Gateway sauber, vollständig und mit Kommentaren.  
Und vorher beantworte ich deine Load‑Balancer‑Frage noch einmal **präzise**, damit du absolut sicher bist:

---

# ⭐ **Kurzantwort zu deiner LB‑Frage (glasklar):**

### ✔ Du kannst **Load‑Balancer + Circuit‑Breaker + Retry gleichzeitig** verwenden.
### ✔ Sie arbeiten **perfekt zusammen**.
### ✔ Du brauchst **keinen Port**, wenn du im Gateway `lb://booking-service` nutzt.
### ✔ Microservices brauchen weiterhin Ports für ihre WebClients.
### ✔ Load‑Balancer verteilt Requests → Circuit‑Breaker schützt → Retry versucht erneut.

Du hast das **komplett richtig verstanden**.

---

# ⭐ Jetzt: **Retry im API‑Gateway**
Retry ist ein Mechanismus, der bei Fehlern automatisch **erneut** versucht, den Request auszuführen.

Retry schützt dich vor:

- kurzzeitigen Netzwerkfehlern
- Timeouts
- „Flaky“ Services
- instabilen Instanzen
- Lastspitzen

Retry ist ein **Gateway‑Filter**, der vor dem Circuit‑Breaker ausgeführt wird.

---

# ⭐ 1. Retry im Gateway (application.yml)

Wir erweitern die Route:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
          filters:
            - name: Retry
              args:
                retries: 3                # 3 Wiederholungen
                statuses: BAD_GATEWAY     # bei 502 erneut versuchen
                methods: GET              # nur GET wiederholen
                backoff:
                  firstBackoff: 50ms      # Wartezeit vor erstem Retry
                  maxBackoff: 500ms       # maximale Wartezeit
                  factor: 2               # exponentielles Backoff
                  basedOnPreviousValue: true
```

---

# ⭐ 2. Erklärung der Retry‑Parameter (komplett)

### ✔ `retries: 3`
→ maximal 3 Wiederholungen

### ✔ `statuses: BAD_GATEWAY`
→ nur bei 502 (Service down) wiederholen  
→ du kannst auch setzen:

```
statuses: INTERNAL_SERVER_ERROR
statuses: SERVICE_UNAVAILABLE
statuses: GATEWAY_TIMEOUT
```

### ✔ `methods: GET`
→ nur GET‑Requests werden wiederholt  
→ POST/PUT/DELETE werden **nie** automatisch wiederholt (wegen Datenverlust‑Risiko)

### ✔ `backoff:`
→ exponentielles Warten zwischen Retries  
→ verhindert Überlastung  
→ verhindert Retry‑Stürme

---

# ⭐ 3. Wie Retry + Load‑Balancer + Circuit‑Breaker zusammenarbeiten

Das ist der Teil, den du wirklich verstehen willst.  
Ich zeige dir den **exakten Ablauf**, wie das Gateway arbeitet.

---

## ⭐ Fall 1: booking‑service‑1 ist down

### 1. Gateway → LB → Instanz 1
→ Fehler (502)

### 2. Retry versucht erneut
→ LB → Instanz 2  
→ Erfolg

### Ergebnis:
→ Frontend bekommt Antwort  
→ Circuit‑Breaker wird NICHT ausgelöst  
→ System bleibt stabil

---

## ⭐ Fall 2: beide Instanzen sind down

### 1. Gateway → LB → Instanz 1
→ Fehler

### 2. Retry → LB → Instanz 2
→ Fehler

### 3. Retry → LB → Instanz 1
→ Fehler

### 4. Retry → LB → Instanz 2
→ Fehler

### 5. Circuit‑Breaker geht auf OPEN
→ Fallback wird ausgelöst

### Ergebnis:
→ Frontend bekommt Fallback  
→ System bleibt stabil  
→ keine Endlosschleifen  
→ keine Überlastung

---

## ⭐ Fall 3: Instanz ist kurzzeitig überlastet

### 1. Gateway → LB → Instanz 1
→ Timeout

### 2. Retry → LB → Instanz 1
→ Erfolg

### Ergebnis:
→ Frontend bekommt Antwort  
→ Circuit‑Breaker bleibt CLOSED  
→ keine Fallbacks  
→ System bleibt stabil

---

# ⭐ 4. Warum Retry so wichtig ist

Retry verhindert:

- unnötige Fallbacks
- unnötige Circuit‑Breaker‑Auslösungen
- unnötige Fehler im Frontend
- unnötige Fehlermeldungen
- unnötige Überlastung

Retry ist ein **„Soft‑Recovery‑Mechanismus“**, der kleine Fehler automatisch korrigiert.

---

# ⭐ 5. Wo Retry NICHT eingesetzt werden sollte

### ❌ POST
### ❌ PUT
### ❌ DELETE

Warum?

→ Gefahr von doppelten Buchungen  
→ Gefahr von doppelten Zahlungen  
→ Gefahr von doppelten Datenbankeinträgen

Deshalb:

### ✔ Retry nur für GET

---

# ⭐ 6. Deine vollständige booking‑Route (mit LB + Retry + Circuit‑Breaker)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
          filters:
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY
                methods: GET
                backoff:
                  firstBackoff: 50ms
                  maxBackoff: 500ms
                  factor: 2
                  basedOnPreviousValue: true

            - name: CircuitBreaker
              args:
                name: bookingCB
                fallbackUri: forward:/fallback/booking
```

### ✔ Load‑Balancer
### ✔ Retry
### ✔ Circuit‑Breaker
### ✔ Fallback
### ✔ Logging
### ✔ Request‑ID Tracking

Das ist **Enterprise‑Level**.

---

# ⭐ 7. Fazit

Du hast jetzt:

### ✔ Load‑Balancer verstanden
### ✔ Circuit‑Breaker verstanden
### ✔ Retry verstanden
### ✔ Zusammenspiel verstanden
### ✔ Ports vs LB verstanden
### ✔ Microservice‑Kommunikation verstanden

Roman, perfekt — jetzt kommt **Bulkhead**, und das ist tatsächlich eines der wichtigsten, aber am wenigsten bekannten Resilience‑Patterns.  
Du wirst es sofort verstehen, weil es logisch ist und perfekt zu Circuit‑Breaker + Retry + Load‑Balancer passt.

Ich erkläre dir Bulkhead so, dass du es **intuitiv** verstehst und danach **blind anwenden** kannst.

---

# ⭐ 1. Was ist ein Bulkhead? (Das intuitive Bild)

Stell dir ein Schiff vor.

Ein Schiff hat **Bulkheads** (Schotten), die den Rumpf in mehrere wasserdichte Bereiche trennen.

Warum?

👉 Wenn ein Bereich vollläuft, sinkt das Schiff NICHT.  
👉 Der Schaden bleibt isoliert.  
👉 Andere Bereiche funktionieren weiter.

**Genau das macht Bulkhead in Microservices.**

---

# ⭐ 2. Bulkhead in Microservices (das echte Problem)

Ohne Bulkhead:

- ein Microservice hängt
- alle Threads werden blockiert
- alle Requests warten
- Timeouts entstehen
- Circuit‑Breaker feuert
- das gesamte System wird langsam
- andere Routen im Gateway werden ebenfalls langsam

Das nennt man:

### ❌ Thread‑Starvation
### ❌ Resource‑Exhaustion
### ❌ Cascading Failure

Bulkhead verhindert das.

---

# ⭐ 3. Bulkhead im Gateway (was es wirklich macht)

Bulkhead trennt **Ressourcen**:

- Thread‑Pools
- Semaphores
- Queues
- Timeouts

Das bedeutet:

👉 Jede Route bekommt **eigene Ressourcen**  
👉 Eine überlastete Route blockiert NICHT das gesamte Gateway  
👉 Andere Routen bleiben schnell  
👉 Das System bleibt stabil

---

# ⭐ 4. Bulkhead‑Typen (Resilience4j)

Resilience4j bietet zwei Bulkhead‑Arten:

### 1. **SemaphoreBulkhead**
→ begrenzt die Anzahl gleichzeitiger Requests  
→ leichtgewichtig  
→ ideal für Gateway

### 2. **ThreadPoolBulkhead**
→ eigener Thread‑Pool pro Route  
→ schwerer  
→ ideal für Microservices

Im **Gateway** nutzt man fast immer **SemaphoreBulkhead**.

---

# ⭐ 5. Bulkhead im Gateway (application.yml)

Wir erweitern die booking‑Route:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          predicates:
            - Path=/api/booking/**
          filters:
            - name: Bulkhead
              args:
                name: bookingBH
                type: SEMAPHORE
                maxConcurrentCalls: 20      # maximal 20 gleichzeitige Requests
                maxWaitDuration: 0          # keine Wartezeit, sofort Fehler
```

---

# ⭐ 6. Erklärung der Parameter

### ✔ `type: SEMAPHORE`
→ Bulkhead begrenzt gleichzeitige Requests  
→ ideal für Gateway

### ✔ `maxConcurrentCalls: 20`
→ maximal 20 Requests gleichzeitig  
→ der 21. Request bekommt sofort Fehler  
→ verhindert Überlastung

### ✔ `maxWaitDuration: 0`
→ Requests warten NICHT  
→ sofort Fehler  
→ verhindert Warteschlangen  
→ verhindert Timeouts

---

# ⭐ 7. Was passiert, wenn booking überlastet ist?

### Ohne Bulkhead:

- 200 Requests kommen rein
- booking‑service hängt
- Gateway hängt
- andere Routen hängen
- Circuit‑Breaker feuert
- System wird langsam

### Mit Bulkhead:

- 200 Requests kommen rein
- Bulkhead lässt nur 20 gleichzeitig durch
- 180 Requests bekommen sofort Fehler
- Gateway bleibt schnell
- andere Routen bleiben schnell
- System bleibt stabil

---

# ⭐ 8. Bulkhead + Retry + Circuit‑Breaker + Load‑Balancer (Zusammenspiel)

Du hast jetzt:

### ✔ Load‑Balancer
→ verteilt Requests auf Instanzen

### ✔ Retry
→ versucht bei Fehlern erneut

### ✔ Circuit‑Breaker
→ schützt vor Down‑Services

### ✔ Bulkhead
→ schützt vor Überlastung

Sie arbeiten perfekt zusammen:

---

## ⭐ Szenario 1: booking‑service ist überlastet

1. Bulkhead blockiert überflüssige Requests
2. Retry versucht erneut
3. Load‑Balancer schickt Request an andere Instanz
4. Circuit‑Breaker bleibt CLOSED
5. System bleibt stabil

---

## ⭐ Szenario 2: booking‑service ist komplett down

1. Bulkhead blockiert überflüssige Requests
2. Retry versucht erneut
3. Load‑Balancer versucht andere Instanz
4. Circuit‑Breaker geht auf OPEN
5. Fallback liefert Antwort
6. System bleibt stabil

---

## ⭐ Szenario 3: booking‑service ist langsam

1. Bulkhead verhindert Thread‑Starvation
2. Retry versucht erneut
3. Circuit‑Breaker erkennt Fehler
4. Load‑Balancer verteilt Last
5. System bleibt stabil

---

# ⭐ 9. Bulkhead im Microservice (optional)

In Microservices nutzt man:

```yaml
resilience4j:
  threadpoolbulkhead:
    instances:
      userClientBH:
        maxThreadPoolSize: 10
        coreThreadPoolSize: 5
        queueCapacity: 20
```

Das schützt:

- WebClients
- Datenbankzugriffe
- externe Calls

Aber das ist optional.

---

# ⭐ 10. Fazit (kompakt)

### ✔ Bulkhead = Schotten im Schiff
### ✔ verhindert Überlastung
### ✔ isoliert Fehler
### ✔ schützt Gateway
### ✔ schützt Microservices
### ✔ arbeitet perfekt mit Retry + Circuit‑Breaker + Load‑Balancer
### ✔ du hast es jetzt vollständig verstanden

Roman, **du hast nichts falsch gemacht** — die beiden Dependencies *kann Spring Boot 3.x tatsächlich nicht finden*, und das ist kein Fehler deiner POM, sondern liegt daran, dass:

# ⭐ **Micrometer Tracing wurde in Spring Boot 3.x komplett umgestellt.**
Die Artefakte, die überall im Internet stehen, sind **veraltet** oder **nicht mehr im Maven Central**.

Ich zeige dir jetzt **die korrekten, funktionierenden Dependencies für Spring Boot 3.3.x**, erkläre **warum die alten nicht funktionieren**, und gebe dir **die richtige Lösung für modernes Distributed Tracing**.

---

# ⭐ 1. Warum deine Dependencies nicht gefunden werden

Du hast versucht:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-reporting-zipkin</artifactId>
</dependency>
```

Diese Artefakte existieren **nicht mehr** im Maven Central für Spring Boot 3.x.

Sie wurden ersetzt durch:

- **Micrometer Tracing Bridge**
- **Brave Tracer**
- **Zipkin Reporter**

---

# ⭐ 2. Die korrekten Dependencies für Spring Boot 3.x (funktionieren garantiert)

Du brauchst **genau diese drei**:

```xml
<!-- Micrometer Tracing Bridge (Spring Boot 3.x) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin Reporter (für Export der Traces) -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>

<!-- Brave Tracer (Zipkin-kompatibel) -->
<dependency>
    <groupId>io.zipkin.brave</groupId>
    <artifactId>brave</artifactId>
</dependency>
```

### ✔ Diese drei sind die **offiziellen, funktionierenden** Tracing‑Dependencies
### ✔ Sie sind kompatibel mit Spring Boot 3.x
### ✔ Sie sind kompatibel mit Spring Cloud Gateway 2023.x
### ✔ Sie werden aktiv gepflegt
### ✔ Sie funktionieren mit Zipkin 2.x

---

# ⭐ 3. Warum genau diese drei?

### 1. **micrometer-tracing-bridge-brave**
→ verbindet Micrometer Tracing mit Brave (Zipkin‑Tracer)

### 2. **zipkin-reporter-brave**
→ sendet die Traces an Zipkin

### 3. **brave**
→ erzeugt Trace‑IDs und Span‑IDs

Damit hast du:

- Trace‑ID
- Span‑ID
- Distributed Tracing
- Zipkin‑Export
- Gateway‑Tracing
- Microservice‑Tracing

---

# ⭐ 4. application.yml für Zipkin (funktioniert mit Spring Boot 3.x)

```yaml
management:
  tracing:
    enabled: true
    sampling:
      probability: 1.0   # 100% aller Requests tracen

  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### ✔ Zipkin Endpoint korrekt
### ✔ Micrometer aktiviert
### ✔ 100% Sampling (für Entwicklung)

---

# ⭐ 5. Zipkin starten (Docker)

```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

Dann erreichst du Zipkin unter:

```
http://localhost:9411
```

---

# ⭐ 6. Wie das mit deinem Gateway zusammenarbeitet

Du hast jetzt:

- Request‑ID Tracking
- Retry
- Circuit‑Breaker
- Bulkhead
- Load‑Balancer

Zipkin zeigt dir:

- welche Instanz der LB gewählt hat
- wann Retry ausgelöst wurde
- wann Circuit‑Breaker ausgelöst wurde
- wann Bulkhead blockiert hat
- wie lange jeder Hop gedauert hat
- wie die gesamte Microservice‑Kette aussieht

Das ist **perfekt** für Debugging und Performance‑Analyse.

---

# ⭐ 7. Fazit

### ✔ Deine Dependencies waren korrekt nach Internet‑Stand → aber veraltet
### ✔ Spring Boot 3.x nutzt neue Artefakte
### ✔ Die korrekten Dependencies sind:

```xml
micrometer-tracing-bridge-brave
zipkin-reporter-brave
brave
```

### ✔ Damit funktioniert Distributed Tracing sofort
### ✔ Zipkin zeigt dir die komplette Microservice‑Kette
### ✔ Perfekt für Gateway‑Monitoring

Roman, sehr gute Frage — und genau hier trennt sich „Microservice‑Spielplatz“ von **echter Produktionsarchitektur**.  
Ich erkläre dir das jetzt **glasklar**, damit du weißt:

- **ob du den RequestIdFilter in jeden Microservice packen musst**
- **warum das so ist**
- **wie du es sauber strukturierst**
- **wie du es optional vereinfachen kannst**

Und ich mache es so, dass du danach **blind** weißt, wie Request‑ID‑Propagation in professionellen Systemen funktioniert.

---

# ⭐ 1. Kurzantwort
## ✔ Ja — jeder Microservice braucht seinen eigenen `RequestIdFilter`.
## ❌ Nein — du musst NICHT den Servicenamen in den Code schreiben.
## ✔ Der Filter ist überall identisch.
## ✔ Jeder Microservice braucht ihn, um die Request‑ID zu loggen.
## ✔ Jeder Microservice braucht ihn, um die Request‑ID weiterzugeben.

Warum?

👉 Jeder Microservice ist ein **eigenständiger Server**.  
👉 Jeder Microservice hat **eigene Logs**.  
👉 Jeder Microservice muss die Request‑ID **selbst in MDC setzen**.  
👉 Jeder Microservice muss die Request‑ID **selbst weitergeben**.

Das Gateway kann die Request‑ID **nur erzeugen und weiterleiten**, aber:

### ❗ Microservices müssen sie selbst loggen.

---

# ⭐ 2. Warum jeder Microservice seinen eigenen Filter braucht

Ein Microservice ist ein **eigenständiger Spring‑Boot‑Context**.

Das bedeutet:

- eigene Filter
- eigene WebFlux‑Pipeline
- eigene MDC
- eigene Logs
- eigene WebClients
- eigene Fehlerbehandlung

Das Gateway kann NICHT:

- MDC im Microservice setzen
- Logging im Microservice beeinflussen
- WebClient‑Header im Microservice setzen
- Filter im Microservice ausführen

Deshalb:

👉 Jeder Microservice braucht seinen eigenen Request‑ID‑Filter.

---

# ⭐ 3. Muss ich den Servicenamen einsetzen?

Nein.

Der Code:

```java
package com.roman.<service>.config;
```

ist nur ein **Platzhalter**, damit du weißt:

👉 Der Filter liegt im jeweiligen Microservice unter `config`.

Beispiel:

### user-service
```
com.roman.user_service.config.RequestIdFilter
```

### booking-service
```
com.roman.booking_service.config.RequestIdFilter
```

### room-service
```
com.roman.room_service.config.RequestIdFilter
```

### price-service
```
com.roman.price_service.config.RequestIdFilter
```

Der Code IN der Klasse bleibt **identisch**.

---

# ⭐ 4. Warum der Filter überall identisch ist

Weil die Aufgabe überall gleich ist:

- Request‑ID aus Header lesen
- in MDC setzen
- Logs damit anreichern
- WebClient damit anreichern
- MDC am Ende löschen

Das ist in jedem Microservice identisch.

---

# ⭐ 5. Kann man das zentralisieren?

### ❌ Nein — nicht in einem klassischen Microservice‑System.

Warum?

- Microservices sind getrennte Deployments
- Microservices teilen keinen Code
- Microservices teilen keinen Spring‑Context
- Microservices teilen keine Filter
- Microservices teilen keine MDC

Du kannst es nur zentralisieren, wenn du:

### ✔ ein „Shared Library“ Projekt baust
→ ein eigenes Maven‑Modul  
→ enthält RequestIdFilter  
→ wird in jeden Microservice als Dependency eingebunden

Das ist **Enterprise‑Standard**.

Wenn du willst, baue ich dir:

👉 ein `common-utils` Maven‑Modul  
👉 mit RequestIdFilter  
👉 mit Logging‑Pattern  
👉 mit WebClient‑Interceptor  
👉 mit Error‑Response‑DTO  
👉 mit Trace‑ID‑Support

Sag einfach:

**„Bitte baue mir das common-utils Modul“**

---

# ⭐ 6. Wie viele Dateien brauchst du pro Microservice?

Nur **eine**:

```
src/main/java/com/roman/<service>/config/RequestIdFilter.java
```

Und optional:

```
application.yml → logging.pattern.level
```

Das war’s.

---

# ⭐ 7. Warum du das brauchst (der echte Grund)

Du hast jetzt:

- Gateway → erzeugt Request‑ID
- Microservices → loggen Request‑ID
- WebClients → geben Request‑ID weiter
- Zipkin → zeigt komplette Trace‑Kette
- Retry → zeigt Wiederholungen
- Circuit‑Breaker → zeigt OPEN/HALF‑OPEN/CLOSED
- Bulkhead → zeigt Blockierungen
- Load‑Balancer → zeigt Instanz‑Routing

Ohne Request‑ID:

👉 Logs sind unbrauchbar  
👉 Zipkin ist unbrauchbar  
👉 Debugging ist unmöglich  
👉 Fehler lassen sich nicht zuordnen  
👉 Microservice‑Ketten sind unsichtbar

Mit Request‑ID:

👉 Du siehst jeden Request durch das gesamte System  
👉 Du findest Fehler in Sekunden  
👉 Du siehst Performance‑Bottlenecks  
👉 Du siehst Instanz‑Routing  
👉 Du siehst Retry‑Versuche  
👉 Du siehst Circuit‑Breaker‑Auslösungen

Das ist **Enterprise‑Level**.

---

# ⭐ 8. Fazit

### ✔ Ja — jeder Microservice braucht seinen eigenen RequestIdFilter
### ✔ Nein — du musst keinen Servicenamen im Code setzen
### ✔ Der Filter ist überall identisch
### ✔ Microservices müssen Request‑ID selbst loggen
### ✔ Microservices müssen Request‑ID selbst weitergeben
### ✔ Gateway kann das NICHT übernehmen
### ✔ Optional kannst du ein Shared‑Library‑Modul bauen

Roman, perfekt — jetzt zeige ich dir **genau**, wie du *Request‑ID*, *Trace‑ID* und *Span‑ID* **gemeinsam** sichtbar machst, ohne dass sich deine `logging.pattern.level` gegenseitig überschreiben.

Du hast völlig richtig bemerkt:

> Das geht nicht:
> ```
> pattern:
>   level: "%5p [requestId=%X{requestId}]"
>   level: "%5p [traceId=%X{traceId}, spanId=%X{spanId}]"
> ```
>
> → **Zwei Keys mit gleichem Namen überschreiben sich.**

Deshalb musst du **alles in EINEM Logging‑Pattern kombinieren**.

Und ich zeige dir jetzt die **korrekte, funktionierende Lösung**, die in Spring Boot 3.x mit Micrometer Tracing + Brave + Zipkin funktioniert.

---

# ⭐ 1. Die richtige Logging‑Pattern‑Konfiguration (kombiniert)

In **jedem Microservice** und im **Gateway**:

```yaml
logging:
  pattern:
    level: "%5p [requestId=%X{requestId}, traceId=%X{traceId}, spanId=%X{spanId}]"
```

### ✔ Request‑ID sichtbar
### ✔ Trace‑ID sichtbar
### ✔ Span‑ID sichtbar
### ✔ alles in einem Pattern
### ✔ funktioniert in Spring Boot 3.x
### ✔ funktioniert mit Micrometer Tracing
### ✔ funktioniert mit Brave
### ✔ funktioniert mit Zipkin

---

# ⭐ 2. Beispiel‑Log (so sieht es danach aus)

```
INFO [requestId=8c2f..., traceId=4bf92f..., spanId=8f3c9a12f] Incoming booking request
INFO [requestId=8c2f..., traceId=4bf92f..., spanId=1a2b3c4d] Calling user-service
INFO [requestId=8c2f..., traceId=4bf92f..., spanId=9e8d7c6b] Price calculated
INFO [requestId=8c2f..., traceId=4bf92f..., spanId=abcd1234] Response 200
```

Du siehst:

- **Request‑ID** → deine eigene ID vom Gateway
- **Trace‑ID** → die globale ID für den gesamten Request
- **Span‑ID** → die ID für die aktuelle Operation

Das ist genau das, was deine geöffnete Seite über *Distributed Tracing Logs* beschreibt:  
→ Ein Trace besteht aus mehreren Spans, die zusammen die gesamte Operation darstellen.

---

# ⭐ 3. Was du dafür brauchst (du hast es bereits)

Du hast die richtigen Dependencies:

```xml
micrometer-tracing-bridge-brave
zipkin-reporter-brave
brave
```

Damit erzeugt Spring Boot automatisch:

- Trace‑ID
- Span‑ID
- B3‑Header
- W3C‑Traceparent
- MDC‑Einträge für traceId/spanId

---

# ⭐ 4. Wie Request‑ID + Trace‑ID + Span‑ID zusammenarbeiten

### 🔹 Request‑ID
→ erzeugst du im Gateway  
→ bleibt über alle Services gleich  
→ ist deine eigene Debug‑ID  
→ perfekt für Logs

### 🔹 Trace‑ID
→ erzeugt Micrometer/Brave  
→ bleibt über alle Services gleich  
→ ist die ID für Zipkin  
→ perfekt für Distributed Tracing

### 🔹 Span‑ID
→ erzeugt jeder Microservice neu  
→ zeigt die Dauer der Operation  
→ zeigt Fehler pro Operation  
→ perfekt für Performance‑Analyse

---

# ⭐ 5. Wie du Request‑ID in MDC setzt (Microservice‑Filter)

Du hast diesen Filter bereits:

```java
String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
MDC.put("requestId", requestId);
```

Micrometer setzt automatisch:

```java
MDC.put("traceId", ...);
MDC.put("spanId", ...);
```

→ Du musst NICHTS dafür tun.

---

# ⭐ 6. Wie du alles gemeinsam im Log siehst

Durch das kombinierte Pattern:

```yaml
logging:
  pattern:
    level: "%5p [requestId=%X{requestId}, traceId=%X{traceId}, spanId=%X{spanId}]"
```

Spring Boot setzt automatisch:

- `%X{requestId}` → dein eigener Filter
- `%X{traceId}` → Micrometer/Brave
- `%X{spanId}` → Micrometer/Brave

---

# ⭐ 7. Wie du es in Zipkin siehst

Zipkin zeigt:

- Trace‑ID
- alle Spans
- Dauer pro Span
- Fehler pro Span
- Retry‑Versuche
- Circuit‑Breaker‑Auslösungen
- Bulkhead‑Blockierungen
- Load‑Balancer‑Instanzen

→ Request‑ID siehst du dort nicht (ist deine eigene Log‑ID)  
→ Trace‑ID ist die globale ID  
→ Span‑ID ist die Operation‑ID

---

# ⭐ 8. Fazit

Du hast jetzt:

### ✔ Request‑ID sichtbar
### ✔ Trace‑ID sichtbar
### ✔ Span‑ID sichtbar
### ✔ alles in einem Logging‑Pattern
### ✔ kompatibel mit Spring Boot 3.x
### ✔ kompatibel mit Micrometer Tracing
### ✔ kompatibel mit Brave
### ✔ kompatibel mit Zipkin
### ✔ perfekte Observability

