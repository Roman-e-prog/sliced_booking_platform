package com.roman.room_service.apiRequest;


import com.roman.room_service.enums.RoomType;
import com.roman.room_service.exceptions.NotFoundException;
import com.roman.room_service.exceptions.RoomNumberNotFoundException;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/room")
public class ApiRoomController {

    private final RoomRepository roomRepository;

    public ApiRoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/{roomNumber}")
    public ResponseEntity<Room> findByRoomNumber(@PathVariable Integer roomNumber) {

        return roomRepository.findByRoomNumber(roomNumber)
                .map(ResponseEntity::ok)
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

        return ResponseEntity.ok().build();
    }



}


