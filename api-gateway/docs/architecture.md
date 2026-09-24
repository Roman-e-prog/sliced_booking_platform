Roman, hier ist deine **komplette, saubere, strukturierte Architektur‑Übersicht**, so wie du sie in deine persönlichen Docs übernehmen kannst.  
Sie ist so geschrieben, dass du sie jederzeit als Referenz nutzen kannst, wenn du Microservices baust, Tests schreibst oder Fehlermeldungen analysierst.

Ich habe alles in **klare Kategorien** gegliedert, damit du es schnell findest und wiederverwenden kannst.

---

# 🧱 **1. Gesamtarchitektur deiner Plattform**

### **Gateway (API‑Gateway)**
- Technologie: **Spring Boot WebFlux**
- Server: **Netty**
- Aufgaben:
  - Routing zu Microservices
  - Authentifizierung (JWT)
  - Refresh‑Token‑Handling
  - CORS
  - Rate‑Limiting
  - Fehlerbehandlung

### **Microservices (User, Room, Price, Booking, Security, etc.)**
- Technologie: **Spring Boot Web (Servlet)**
- Server: **Tomcat**
- Aufgaben:
  - Fachlogik
  - Datenbankzugriff
  - Validierung
  - Security (MethodSecurity)
  - WebClient‑Calls zu anderen Services

### **Kommunikation**
- **WebClient** (reactive client, aber im Servlet‑Service völlig ok)
- **Kafka** (Events)
- **REST** (synchron)

---

# ⚙️ **2. Dependencies – Was du wofür brauchst**

## **A. Gateway (WebFlux‑Server)**
| Zweck | Dependency |
|------|------------|
| WebFlux‑Server | `spring-boot-starter-webflux` |
| Security | `spring-boot-starter-security` |
| JWT | `jjwt-*` |
| Tracing | `micrometer-tracing-bridge-brave`, `zipkin-reporter-brave` |
| WebClient | automatisch enthalten |

---

## **B. Microservices (Servlet‑Server)**

### **1. Web / Controller / MVC**
```xml
spring-boot-starter-web
```

### **2. WebClient (Client, NICHT Server!)**
```xml
spring-webflux
```
**Wichtig:**  
- Aktiviert NICHT den WebFlux‑Server  
- Bringt NUR WebClient + Mono/Flux  
- Sicher im Servlet‑Modus

### **3. Security**
```xml
spring-boot-starter-security
```

### **4. JPA / Hibernate**
```xml
spring-boot-starter-data-jpa
hibernate-core
postgresql
```

### **5. Validation**
```xml
spring-boot-starter-validation
```

### **6. Flyway**
```xml
flyway-core
flyway-database-postgresql
```

### **7. Kafka**
```xml
spring-kafka
```

### **8. Tracing (optional, aber NICHT empfohlen in Microservices!)**
❌ NICHT in Microservices verwenden  
✔ Nur im Gateway

---

# 🔌 **3. WebClient – Wann du was brauchst**

| Was? | Wofür? | Wann? |
|------|--------|-------|
| **spring-webflux** | WebClient API | IMMER, wenn du WebClient nutzt |
| **reactor-netty** | HTTP‑Transport | Wird automatisch von spring-webflux eingebunden |
| **spring-boot-starter-webflux** | WebFlux‑Server | Nur im Gateway, NIE in Microservices |

### **Merksatz:**
> **WebClient = spring-webflux (Framework), nicht spring-boot-starter-webflux.**

---

# 🧪 **4. Tests – Welche Annotation du wann verwendest**

## **A. `@WebMvcTest` – Nur für sehr einfache Controller**
✔ Nur Controller  
✔ Keine Services  
✔ Keine Repositories  
✔ Keine WebClient‑Beans  
✔ Keine Multipart‑Requests  
✔ Keine Security  
✔ Keine komplexe AutoConfig

**Wenn du WebClient im Projekt hast → lädt WebFlux‑Test‑AutoConfig → bricht.**

---

## **B. `@SpringBootTest` – Für echte Controller**
✔ Lädt gesamten Kontext  
✔ MockMvc funktioniert  
✔ Multipart funktioniert  
✔ WebClient stört nicht  
✔ Security kann deaktiviert werden

**Empfohlen für deine RoomController‑Tests.**

---

## **C. `@MockBean` statt `@MockitoBean`**
✔ Ersetzt echte Beans im Kontext  
✔ Funktioniert mit SpringBootTest  
✔ Verhindert ApplicationContext‑Fehler

---

# 🧭 **5. Typische Fehlermeldungen und ihre Bedeutung**

### **1. `ResourceHttpRequestHandler`**
- Controller wurde NICHT gefunden
- Pfad falsch
- MVC deaktiviert (WebFlux‑Server aktiv)
- Security blockiert

### **2. `NoResourceFoundException`**
- Spring sucht statische Datei
- Controller wurde NICHT gemappt

### **3. `ApplicationContext failure threshold exceeded`**
- Test lädt zu viele AutoConfigs
- WebFlux‑Test‑AutoConfig kollidiert mit MockMvc
- Multipart + WebClient + WebMvcTest = Konflikt

### **4. `Package org.springframework.web.reactive.function.client ist nicht vorhanden`**
- spring-webflux fehlt
- WebClient kann nicht kompiliert werden

### **5. `Invalid email or password` vs. `User not found`**
- Service wirft andere Fehlermeldung als Test erwartet
- Tests müssen angepasst werden oder Service

### **6. `DateTimeParseException: Instant.parse("1990-01-01")`**
- Instant erwartet Zeit + Zone
- Lösung: `LocalDate.parse()`

---

# 🧩 **6. Checkliste für Microservices**

### **A. pom.xml**
- ✔ `spring-boot-starter-web`
- ✔ `spring-webflux`
- ✔ `spring-boot-starter-security`
- ✔ JPA + Hibernate
- ✔ Flyway
- ✔ Kafka
- ❌ KEIN `spring-boot-starter-webflux`
- ❌ KEIN Tracing (nur im Gateway)

### **B. Controller**
- ✔ `/api/...` Prefix
- ✔ DTOs statt Entities
- ✔ ExceptionHandler global

### **C. WebClient**
- ✔ Eigene Config pro Service
- ✔ BaseUrl setzen
- ✔ Timeout setzen
- ✔ Retry setzen

### **D. Tests**
- ✔ `@SpringBootTest` für echte Controller
- ✔ `@MockBean` für Services
- ✔ `@AutoConfigureMockMvc(addFilters=false)`
- ❌ Kein `@WebMvcTest` bei WebClient + Multipart

---

# 🏗️ **7. Architektur‑Diagramm (Textform)**

```
                        ┌──────────────────────────┐
                        │        API Gateway        │
                        │   (WebFlux, Netty)        │
                        └────────────┬─────────────┘
                                     │
                                     ▼
        ┌──────────────────────────────────────────────────────┐
        │                    Microservices                     │
        │                                                      │
        │  User-Service        Room-Service        Price-Service│
        │  (Servlet/Tomcat)    (Servlet/Tomcat)    (Servlet/Tomcat)
        │  spring-webflux → WebClient only                     │
        └──────────────────────────────────────────────────────┘
                                     │
                                     ▼
                        ┌──────────────────────────┐
                        │        PostgreSQL         │
                        └──────────────────────────┘

                        ┌──────────────────────────┐
                        │          Kafka            │
                        └──────────────────────────┘
```

---

Roman, das ist jetzt deine **komplette, saubere, professionelle Architektur‑Übersicht**, die du direkt in deine persönlichen Docs übernehmen kannst.

Roman, hier bekommst du **alle Checklisten**, vollständig, sauber strukturiert und so formuliert, dass du sie direkt in deine persönlichen Docs übernehmen kannst.  
Sie sind bewusst **praxisorientiert**, damit du sie beim Entwickeln, Debuggen und Testen sofort anwenden kannst.

---

# 🧭 **CHECKLISTE 1 — Microservice‑Architektur (Servlet + WebFlux‑Client)**

### ✔ Gateway (WebFlux‑Server)
- `spring-boot-starter-webflux`
- `spring-boot-starter-security`
- JWT‑Handling
- Refresh‑Token‑Handling
- Routing
- CORS
- Tracing (Zipkin/Brave)
- WebClient für Downstream‑Calls

### ✔ Microservices (Servlet‑Server)
- `spring-boot-starter-web` (Tomcat)
- `spring-webflux` (für WebClient)
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `hibernate-core`
- `postgresql`
- `spring-kafka`
- `spring-boot-starter-validation`
- `flyway-core`

### ❌ NICHT in Microservices:
- `spring-boot-starter-webflux`  
- Zipkin/Brave Tracing  
- Sleuth  
- Reactive Security  
- Reactive Repositories  

---

# ⚙️ **CHECKLISTE 2 — Dependencies: Was wofür?**

### ✔ Web (Servlet)
`spring-boot-starter-web`  
→ MVC, DispatcherServlet, Tomcat, MockMvc

### ✔ WebClient
`spring-webflux`  
→ WebClient, Mono/Flux, WebClientResponseException

### ✔ Security
`spring-boot-starter-security`  
→ SecurityFilterChain, MethodSecurity

### ✔ Datenbank
`spring-boot-starter-data-jpa`  
`hibernate-core`  
`postgresql`

### ✔ Migration
`flyway-core`  
`flyway-database-postgresql`

### ✔ Messaging
`spring-kafka`

### ✔ Validation
`spring-boot-starter-validation`

### ❌ NICHT verwenden:
`spring-boot-starter-webflux` (nur Gateway)

---

# 🔌 **CHECKLISTE 3 — WebClient richtig einsetzen**

### ✔ WebClient benötigt:
- `spring-webflux` (Framework)
- Reactor Netty wird automatisch eingebunden

### ✔ WebClient ist sicher im Servlet‑Modus

### ✔ WebClient‑Config pro Service:
- BaseUrl
- Timeout
- Retry
- Error‑Handling

### ❌ NICHT verwenden:
- `spring-boot-starter-webflux`  
→ aktiviert WebFlux‑Server → deaktiviert MVC → zerstört Tests

### Merksatz:
> **WebClient = spring-webflux (Framework), nicht spring-boot-starter-webflux.**

---

# 🧪 **CHECKLISTE 4 — Tests richtig aufsetzen**

## ✔ A. Wann `@WebMvcTest`?
Nur wenn der Controller:
- KEINE Services injiziert
- KEINE Repositories injiziert
- KEINE WebClient‑Beans nutzt
- KEINE Multipart‑Requests verarbeitet
- KEINE Security nutzt
- KEINE ExceptionHandler nutzt
- KEINE komplexe AutoConfig benötigt

### ❌ Wenn WebClient im Projekt ist → lädt WebFlux‑Test‑AutoConfig → bricht.

---

## ✔ B. Wann `@SpringBootTest`?
Wenn der Controller:
- Services injiziert
- Repositories injiziert
- WebClient nutzt
- Multipart verarbeitet
- Security nutzt
- ExceptionHandler nutzt

### Empfohlen für RoomController, UserController, BookingController etc.

---

## ✔ C. Mocking
- `@MockBean` → ersetzt echte Beans im Kontext
- `@AutoConfigureMockMvc(addFilters=false)` → Security deaktivieren

---

## ✔ D. Multipart‑Tests
Nur mit:
- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- NICHT mit `@WebMvcTest`

---

# 🧩 **CHECKLISTE 5 — Controller‑Mapping**

### ✔ Prefix immer `/api/...`
Beispiel:
```
@RequestMapping("/api/user")
@RequestMapping("/api/rooms")
@RequestMapping("/api/booking")
```

### ✔ Tests müssen exakt denselben Pfad verwenden

### ❌ Häufiger Fehler:
Test ruft `/user/all`, Controller hört auf `/api/user/all`.

---

# 🔐 **CHECKLISTE 6 — Security**

### ✔ Security im Test deaktivieren:
```java
@AutoConfigureMockMvc(addFilters = false)
```

### ✔ MethodSecurity aktivieren:
```java
@EnableMethodSecurity
```

### ✔ PreAuthorize korrekt:
```java
@PreAuthorize("@userSecurity.isOwner(#userId) or hasRole('Admin')")
```

### ❌ NICHT im Test:
- echte JWTs
- echte SecurityFilterChain

---

# 🧱 **CHECKLISTE 7 — Fehlermeldungen richtig lesen**

## ❗ 1. `ResourceHttpRequestHandler`
→ Controller wurde NICHT gefunden  
→ Pfad falsch oder MVC deaktiviert

## ❗ 2. `NoResourceFoundException`
→ Spring sucht statische Datei  
→ Controller nicht gemappt

## ❗ 3. `ApplicationContext failure threshold exceeded`
→ Test lädt zu viele AutoConfigs  
→ WebFlux‑Test‑AutoConfig kollidiert mit MockMvc  
→ Lösung: `@SpringBootTest`

## ❗ 4. `Package org.springframework.web.reactive.function.client ist nicht vorhanden`
→ spring-webflux fehlt  
→ WebClient kann nicht kompiliert werden

## ❗ 5. `Invalid email or password` vs. `User not found`
→ Service wirft andere Fehlermeldung als Test erwartet  
→ Tests oder Service angleichen

## ❗ 6. `DateTimeParseException: Instant.parse("1990-01-01")`
→ Instant erwartet Zeit + Zone  
→ Lösung: `LocalDate.parse()`

---

# 🧠 **CHECKLISTE 8 — Fehlersuche (Debug‑Flow)**

### ✔ Schritt 1 — Logs prüfen
- Wird Controller gemappt?
- Wird Service ausgeführt?

### ✔ Schritt 2 — Pfade prüfen
- `/api/...` korrekt?

### ✔ Schritt 3 — Test‑Annotation prüfen
- `@WebMvcTest` oder `@SpringBootTest`?

### ✔ Schritt 4 — Dependencies prüfen
- spring-webflux drin?
- spring-boot-starter-webflux NICHT drin?

### ✔ Schritt 5 — Security prüfen
- addFilters=false?

### ✔ Schritt 6 — Multipart prüfen
- Nur mit SpringBootTest

### ✔ Schritt 7 — Fehlermeldung kategorisieren
- ResourceHandler?
- ApplicationContext?
- Missing package?
- Wrong exception message?

---

# 🏗️ **CHECKLISTE 9 — Projektstruktur**

### ✔ Gateway
- WebFlux
- Security
- JWT
- Routing
- WebClient

### ✔ Microservices
- Servlet
- WebClient
- JPA
- Flyway
- Kafka
- Security
- DTOs
- ExceptionHandler

### ✔ Shared Concepts
- DTOs
- Error‑Model
- Token‑Model
- WebClient‑Config

---

Roman, das ist jetzt deine **komplette Sammlung aller Checklisten**, die du jederzeit nutzen kannst, um:

- Fehler schneller zu finden  
- Tests korrekt aufzusetzen  
- Microservices sauber zu bauen  
- WebClient richtig zu verwenden  
- Controller korrekt zu testen  
- Dependencies richtig zu wählen  

