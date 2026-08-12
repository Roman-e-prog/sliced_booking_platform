package com.roman.booking_service.booking.service;
import com.roman.booking_service.webClient.client.RoomClient;
import com.roman.booking_service.webClient.client.SecurityClient;
import com.roman.booking_service.webClient.client.PriceClient;
import com.roman.booking_service.webClient.client.UserClient;
import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.booking.mapper.BookingMapper;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.exceptions.RoomNumberNotFoundException;
import com.roman.booking_service.booking.repository.BookingRepository;
import com.roman.booking_service.booking.dto.BookingRequest;
import com.roman.booking_service.webClient.dto.PriceResponse;
import com.roman.booking_service.webClient.dto.RoomResponse;
import com.roman.booking_service.webClient.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.roman.booking_service.exceptions.NotFoundException;
import com.roman.booking_service.exceptions.PriceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.roman.booking_service.services.EmailService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class BookingService {
    //variable setting for getting repositories
    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final SecurityClient securityClient;
    private final PriceClient priceClient;
    private final RoomClient roomClient;
    private final EmailService emailService;
    private final BookingMapper bookingMapper;
    //initialize repository
    public BookingService(BookingRepository bookingRepository,
                          UserClient userClient, SecurityClient securityClient,
                          BookingMapper bookingMapper,
                          PriceClient priceClient,
                          RoomClient roomClient, EmailService emailService){
        this.bookingRepository = bookingRepository;
        this.userClient = userClient;
        this.securityClient = securityClient;
        this.bookingMapper = bookingMapper;
        this.priceClient = priceClient;
        this.roomClient = roomClient;
        this.emailService = emailService;
    }
    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }
    // ----------------------------- // FIND BY ID (throws if not found) // -----------------------------
    @Transactional(readOnly = true)
    public BookingResponse findByIdOrThrow(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(bookingId, "Booking"));

        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }


    //create
    public Booking bookRoom(BookingRequest request){
        System.out.println("BookingService.bookRoom called with request: " + request);
        // 1. Eingeloggten User holen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        UserResponse user = userClient.fetchUserById(userId);

        Booking booking = new Booking();

        booking.setUserId(user.userId()); // 2. User automatisch setzen

        booking.setStartDate(LocalDate.parse(request.getStartDate()));
        booking.setEndDate(LocalDate.parse(request.getEndDate()));
        booking.setNumberOfPersons(request.getNumberOfPersons());
        booking.setBookingType(request.getBookingType());
        booking.setUserType(request.getUserType());
        booking.setRoomType(request.getRoomType());
        booking.setRoomNumber(request.getRoomNumber());
        // Preisberechnung
        String roomType = String.valueOf(booking.getRoomType());
        String bookingType = String.valueOf(booking.getBookingType());
        PriceResponse priceConfig = priceClient
                .findByRoomTypeAndBookingType(roomType, bookingType)
                .orElseThrow(() -> new PriceNotFoundException(booking.getRoomType(), booking.getBookingType()));

        long nights = DAYS.between(booking.getStartDate(), booking.getEndDate());

        booking.setPricePerNight(priceConfig.nettoPrice());
        booking.setFullPrice(priceConfig.nettoPrice().multiply(BigDecimal.valueOf(nights)));

        // Steuer: 19 → 0.19
        BigDecimal taxRateDecimal = priceConfig.taxRate()
                .divide(BigDecimal.valueOf(100)); 

        // Steuerbetrag
        BigDecimal tax = booking.getFullPrice().multiply(taxRateDecimal);
        booking.setTax(tax);

        // Brutto
        booking.setBruttoPrice(booking.getFullPrice().add(tax));
        //Bestätigung
        emailService.sendBookingConfirmation(
                user.email(),
                "Ihre Buchung wurde aktualisiert",
                "Hallo Herr" + user.lastname() + ",\n\n" +
                        "Ihre Buchung wurde erfolgreich durchgeführt.\n" +
                        "Sie erhalten in Kürze Ihre Zimmernummer " +
                        "Viele Grüße,\nIhr Hotel-Team"
        );

        return bookingRepository.save(booking);
    }


    // ----------------------------- //
    // UPDATE // 
    // ----------------------------- 
   @Transactional
public Booking updateBooking(Long bookingId, BookingRequest request) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException(bookingId, "Booking"));

    // Admin weist Zimmer zu
    if (request.getRoomNumber() != null) {

        RoomResponse room = roomClient.fetchRoomByNumber(request.getRoomNumber());
        if (room == null || !room.isAvailable()) {
            throw new RoomNumberNotFoundException(request.getRoomNumber());
        }
        // Zimmer belegen
        boolean updated = roomClient.updateRoomAvailability(request.getRoomNumber(), false);
        if(!updated){
            throw new IllegalStateException("Raumvergabe hat nicht funktioniert");
        }
        else{
            // Zimmernummer in Buchung setzen
            booking.setRoomNumber(request.getRoomNumber());
        }
    }
    // user holen
       UserResponse user = userClient.fetchUserById(booking.getUserId());
    // Buchungsdaten aktualisieren
    booking.setStartDate(LocalDate.parse(request.getStartDate()));
    booking.setEndDate(LocalDate.parse(request.getEndDate()));
    booking.setNumberOfPersons(request.getNumberOfPersons());
    booking.setBookingType(request.getBookingType());
    booking.setUserType(request.getUserType());
    booking.setRoomType(request.getRoomType());

    // Preis neu berechnen
       // Preisberechnung
       String roomType = String.valueOf(booking.getRoomType());
       String bookingType = String.valueOf(booking.getBookingType());
       PriceResponse priceConfig = priceClient
               .findByRoomTypeAndBookingType(roomType, bookingType)
               .orElseThrow(() -> new PriceNotFoundException(
                       booking.getRoomType(), booking.getBookingType()));

       long nights = DAYS.between(booking.getStartDate(), booking.getEndDate());

       booking.setPricePerNight(priceConfig.nettoPrice());
       booking.setFullPrice(priceConfig.nettoPrice().multiply(BigDecimal.valueOf(nights)));

       // Steuer: 19 → 0.19
       BigDecimal taxRateDecimal = priceConfig.taxRate()
               .divide(BigDecimal.valueOf(100));

       BigDecimal tax = booking.getFullPrice().multiply(taxRateDecimal);

    booking.setTax(tax);
    booking.setBruttoPrice(booking.getFullPrice().add(tax));
       Booking saved = bookingRepository.save(booking);
       emailService.sendBookingConfirmation(
               user.email(),
               "Ihre Buchung wurde aktualisiert",
               "Hallo Herr" + user.lastname() + ",\n\n" +
                       "Ihre Buchung wurde erfolgreich aktualisiert.\n" +
                       "Neue Zimmernummer: " + booking.getRoomNumber() + "\n\n" +
                       "Viele Grüße,\nIhr Hotel-Team"
       );


       return saved;
}

    // ----------------------------- // DELETE // -----------------------------
    @Transactional
public void deleteBooking(Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException(bookingId, "Booking"));

        // Wenn die Buchung ein Zimmer hat → Zimmer wieder freigeben
        if (booking.getRoomNumber() != null) {

                RoomResponse room = roomClient.fetchRoomByNumber(booking.getRoomNumber());

                roomClient.updateRoomAvailability(room.roomNumber(),true);
        }

        bookingRepository.delete(booking);
        }

}
