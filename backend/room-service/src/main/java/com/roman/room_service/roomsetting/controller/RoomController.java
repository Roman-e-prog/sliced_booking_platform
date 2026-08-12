package com.roman.room_service.roomsetting.controller;

import com.roman.room_service.roomsetting.dto.RoomResponse;
import com.roman.room_service.roomsetting.dto.RoomUpdateRequest;
import com.roman.room_service.roomsetting.dto.RoomWithImagesResponse;
import com.roman.room_service.roomsetting.mapper.RoomMapper;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.service.RoomService;
import com.roman.room_service.roomsetting.dto.RoomRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }
    //create
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(
            @RequestPart(value = "data", required = false) RoomRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {

        if (request != null) {
            request.setImages(images);
            Room room = roomService.createRoom(request);
            return ResponseEntity.ok(RoomMapper.toResponse(room));
        }

        throw new IllegalArgumentException("Missing multipart data");
    }


    //update
    @PostMapping(value= "/{roomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestPart("data") RoomUpdateRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        System.out.println(">>> Controller reached");

        Room room = roomService.updateRoom(roomId, request, images);
        return ResponseEntity.ok(RoomMapper.toResponse(room));
    }

    @GetMapping("/{id}")
    public RoomWithImagesResponse getRoom(@PathVariable Long id) {
        return roomService.findOrThrow(id);
    }

    @GetMapping("/all") public List<RoomWithImagesResponse> getAllRooms() {
        return roomService.findAllRooms();
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId
    ){
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
