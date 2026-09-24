package com.roman.room_service.apiRequest;


import com.roman.room_service.enums.RoomType;
import com.roman.room_service.exceptions.NotFoundException;
import com.roman.room_service.exceptions.RoomNumberNotFoundException;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.roman.room_service.roomsetting.mapper.RoomMapper;
import com.roman.room_service.roomsetting.dto.RoomResponse;
import com.roman.events.RoomAvailabilityEvent;
import com.roman.room_service.kafka.RoomEventPublisher;
import java.math.BigDecimal;

@RestController
@RequestMapping("/room")
public class ApiRoomController {

    private final RoomRepository roomRepository;
    private final RoomEventPublisher roomEventPublisher;
    public ApiRoomController(RoomRepository roomRepository, RoomEventPublisher roomEventPublisher) {
        this.roomRepository = roomRepository;
        this.roomEventPublisher = roomEventPublisher;
    }

    @GetMapping("/{roomNumber}")
        public ResponseEntity<RoomResponse> findByRoomNumber(@PathVariable Integer roomNumber) {

    return roomRepository.findByRoomNumber(roomNumber)
            .map(room -> ResponseEntity.ok(RoomMapper.toResponse(room)))
            .orElse(ResponseEntity.notFound().build());
    }
    @PatchMapping("/{roomNumber}/availability")
        public ResponseEntity<Void> updateAvailability(
                @PathVariable Integer roomNumber,
                @RequestParam boolean available
        ) {
            Room room = roomRepository.findByRoomNumber(roomNumber)
                    .orElseThrow(() -> new RoomNumberNotFoundException(roomNumber));

            room.setIsAvailable(available);
            roomRepository.save(room);

            // Event publizieren
            roomEventPublisher.publish(new RoomAvailabilityEvent(roomNumber, available));

            return ResponseEntity.ok().build();
        }
}


