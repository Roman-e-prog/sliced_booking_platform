package com.roman.booking_service.booking.service;

import com.roman.booking_service.booking.dto.BookingRequest;
import com.roman.booking_service.booking.mapper.BookingMapper;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.booking.repository.BookingRepository;
import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.enums.UserType;
import com.roman.booking_service.services.EmailService;
import com.roman.booking_service.webClient.client.PriceClient;
import com.roman.booking_service.webClient.client.RoomClient;
import com.roman.booking_service.webClient.client.SecurityClient;
import com.roman.booking_service.webClient.client.UserClient;
import com.roman.booking_service.webClient.dto.PriceResponse;
import com.roman.booking_service.webClient.dto.RoomResponse;
import com.roman.booking_service.webClient.dto.UserResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    // ---------------------------------------------------------
    // MOCKED DEPENDENCIES
    // ---------------------------------------------------------
    @Mock private BookingRepository bookingRepository;
    @Mock private UserClient userClient;
    @Mock private PriceClient priceClient;
    @Mock private RoomClient roomClient;
    @Mock private SecurityClient securityClient;
    @Mock private BookingMapper bookingMapper;
    @Mock private EmailService emailService;

    // Service under test
    @InjectMocks
    private BookingService bookingService;

    // ---------------------------------------------------------
    // SECURITY CONTEXT SETUP
    // ---------------------------------------------------------
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Simuliere eingeloggten User → principal = userId (Long)
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    // ---------------------------------------------------------
    // CREATE BOOKING
    // ---------------------------------------------------------
    @Test
    void should_create_a_roomBooking() {

        // ARRANGE → BookingRequest vorbereiten
        BookingRequest request = new BookingRequest();
        request.setBookingType(BookingType.WITH_BREAKFAST);
        request.setUserType(UserType.BUSINESS_GUEST);
        request.setStartDate("2026-03-01");
        request.setEndDate("2026-03-15");
        request.setNumberOfPersons(1);
        request.setRoomType(RoomType.ONE_BED);
        request.setRoomNumber(101);

        // UserClient → Userdaten mocken
        UserResponse user = new UserResponse(
                1L, "Roman", "Mustermann", "roman123",
                "Street", "1", 12345, "Town", "Country",
                "roman@example.com", LocalDate.of(1990,1,1),
                "USER", LocalDateTime.now()
        );
        when(userClient.fetchUserById(1L)).thenReturn(user);

        // PriceClient → Preis-Konfiguration mocken
        PriceResponse priceConfig = new PriceResponse(
                1L,
                RoomType.ONE_BED,
                BookingType.WITH_BREAKFAST,
                BigDecimal.valueOf(250),
                BigDecimal.valueOf(19)
        );
        when(priceClient.findByRoomTypeAndBookingType("ONE_BED", "WITH_BREAKFAST"))
                .thenReturn(Optional.of(priceConfig));

        // Repository → Speichern mocken
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // ACT → Buchung erstellen
        Booking result = bookingService.bookRoom(request);

        // ASSERT → Preisberechnung prüfen
        long nights = ChronoUnit.DAYS.between(
                LocalDate.parse("2026-03-01"),
                LocalDate.parse("2026-03-15")
        );

        BigDecimal expectedFullPrice = BigDecimal.valueOf(250).multiply(BigDecimal.valueOf(nights));
        BigDecimal expectedTax = expectedFullPrice.multiply(BigDecimal.valueOf(0.19));
        BigDecimal expectedBrutto = expectedFullPrice.add(expectedTax);

        assertEquals(expectedFullPrice, result.getFullPrice());
        assertEquals(expectedTax, result.getTax());
        assertEquals(expectedBrutto, result.getBruttoPrice());
        assertEquals(1L, result.getUserId());

        // E-Mail muss gesendet werden
        verify(emailService).sendBookingConfirmation(
                eq("roman@example.com"),
                anyString(),
                anyString()
        );
    }

    // ---------------------------------------------------------
    // UPDATE BOOKING
    // ---------------------------------------------------------
   @Test
void should_update_a_booking() {

    // EXISTING BOOKING → wird aus DB geladen
    Booking existing = new Booking();
    existing.setBookingId(1L);
    existing.setUserId(1L);
    existing.setStartDate(LocalDate.parse("2026-03-01"));
    existing.setEndDate(LocalDate.parse("2026-03-05"));
    existing.setNumberOfPersons(1);
    existing.setBookingType(BookingType.ONLY_REST);
    existing.setUserType(UserType.PRIVATE_GUEST);
    existing.setRoomType(RoomType.ONE_BED);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));

    // ROOM CLIENT → Zimmer holen und als verfügbar markieren
    RoomResponse room = new RoomResponse(
            1L,
            RoomType.ONE_BED,
            true,                      // isAvailable = true → sonst wirft der Service Exception
            "Testbeschreibung",
            BigDecimal.valueOf(250.00),
            101,
            LocalDateTime.now()
    );
    when(roomClient.fetchRoomByNumber(101)).thenReturn(room);
    when(roomClient.updateRoomAvailability(101, false)).thenReturn(true);

    // USER CLIENT → Userdaten holen
    UserResponse user = new UserResponse(
            1L, "Roman", "Mustermann", "roman123",
            "Street", "1", 12345, "Town", "Country",
            "roman@example.com", LocalDate.of(1990,1,1),
            "USER", LocalDateTime.now()
    );
    when(userClient.fetchUserById(1L)).thenReturn(user);

    // PRICE CLIENT → Preis-Konfiguration mocken
    PriceResponse priceConfig = new PriceResponse(
            1L,
            RoomType.ONE_BED,
            BookingType.WITH_BREAKFAST,
            BigDecimal.valueOf(250),
            BigDecimal.valueOf(19)
    );
    when(priceClient.findByRoomTypeAndBookingType("ONE_BED", "WITH_BREAKFAST"))
            .thenReturn(Optional.of(priceConfig));

    // SAVE → aktualisierte Buchung speichern
    when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // REQUEST → neue Buchungsdaten
    BookingRequest request = new BookingRequest();
    request.setRoomNumber(101);
    request.setStartDate("2026-03-10");
    request.setEndDate("2026-03-20");
    request.setNumberOfPersons(2);
    request.setBookingType(BookingType.WITH_BREAKFAST);
    request.setUserType(UserType.BUSINESS_GUEST);
    request.setRoomType(RoomType.ONE_BED);

    // ACT → Buchung aktualisieren
    Booking result = bookingService.updateBooking(1L, request);

    // ASSERT → Felder prüfen
    assertEquals(101, result.getRoomNumber());
    assertEquals(LocalDate.parse("2026-03-10"), result.getStartDate());
    assertEquals(LocalDate.parse("2026-03-20"), result.getEndDate());
    assertEquals(2, result.getNumberOfPersons());
    assertEquals(BookingType.WITH_BREAKFAST, result.getBookingType());

    long nights = ChronoUnit.DAYS.between(result.getStartDate(), result.getEndDate());
    BigDecimal expectedFull = BigDecimal.valueOf(250).multiply(BigDecimal.valueOf(nights));
    BigDecimal expectedTax = expectedFull.multiply(BigDecimal.valueOf(0.19));
    BigDecimal expectedBrutto = expectedFull.add(expectedTax);

    assertEquals(expectedFull, result.getFullPrice());
    assertEquals(expectedTax, result.getTax());
    assertEquals(expectedBrutto, result.getBruttoPrice());

    // optional: sicherstellen, dass der Patch wirklich aufgerufen wurde
    verify(roomClient).updateRoomAvailability(101, false);
}


    // ---------------------------------------------------------
    // DELETE BOOKING
    // ---------------------------------------------------------
    @Test
    void should_delete_a_booking() {

        // EXISTING BOOKING → wird aus DB geladen
        Booking existing = new Booking();
        existing.setBookingId(1L);
        existing.setRoomNumber(101);

        // ROOM CLIENT → Zimmerdaten holen
        RoomResponse room = new RoomResponse(
                1L,
                RoomType.ONE_BED,
                true,
                "Testbeschreibung",
                BigDecimal.valueOf(250.00),
                101,
                LocalDateTime.now()
        );

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomClient.fetchRoomByNumber(101)).thenReturn(room);

        // ACT → Buchung löschen
        bookingService.deleteBooking(1L);

        // ASSERT → Buchung gelöscht + Zimmer freigegeben
        verify(bookingRepository).delete(existing);
        verify(roomClient).updateRoomAvailability(101, true);
    }
}
