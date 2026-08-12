package com.roman.room_service.roomsetting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.room_service.roomsetting.dto.*;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.repository.RoomRepository;
import com.roman.room_service.roomsetting.service.RoomService;
//import com.roman.room_service.security.jwt.JwtService;
//import com.roman.room_service.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import com.roman.room_service.enums.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2
@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoomSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private RoomRepository roomRepository;

//    @MockitoBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // CREATE ROOM
    // ---------------------------------------------------------
    @Test
    void should_create_a_room() throws Exception {

        MockMultipartFile img1 = new MockMultipartFile(
                "images", "test1.jpg", "image/jpeg", "dummy".getBytes()
        );

        MockMultipartFile json = new MockMultipartFile(
                "data", "", "application/json",
                """
                {
                  "description": "A Test description",
                  "roomType": "ONE_BED",
                  "isAvailable": true,
                  "pricePerNight": 240.00,
                  "roomNumber": 1
                }
                """.getBytes()
        );

        Room room = new Room();
        room.setRoomId(1L);
        room.setDescription("A Test description");
        room.setRoomType(RoomType.ONE_BED);
        room.setIsAvailable(true);
        room.setPricePerNight(BigDecimal.valueOf(240));
        room.setRoomNumber(1);

        when(roomService.createRoom(any(RoomRequest.class))).thenReturn(room);

        mockMvc.perform(
                        multipart("/rooms/roomSetting")
                                .file(img1)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.description").value("A Test description"))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"))
                .andExpect(jsonPath("$.isAvailable").value(true))
                .andExpect(jsonPath("$.roomNumber").value(1));

        verify(roomService).createRoom(any(RoomRequest.class));
    }

    // ---------------------------------------------------------
    // UPDATE ROOM (Images ersetzen)
    // ---------------------------------------------------------
    @Test
    void should_update_images() throws Exception {

        MockMultipartFile img1 = new MockMultipartFile(
                "images", "test1.jpg", "image/jpeg", "dummy".getBytes()
        );

        MockMultipartFile img2 = new MockMultipartFile(
                "images", "test2.jpg", "image/jpeg", "dummy".getBytes()
        );

        MockMultipartFile json = new MockMultipartFile(
                "data", "", "application/json",
                """
                {
                  "description": "A Test description",
                  "roomType": "ONE_BED",
                  "isAvailable": true,
                  "pricePerNight": 240.00,
                  "roomNumber": 1,
                  "images": [
                    { "imageId": 10, "alt": "Alt 1", "title": "Title 1" },
                    { "imageId": 11, "alt": "Alt 2", "title": "Title 2" }
                  ]
                }
                """.getBytes()
        );

        Room room = new Room();
        room.setRoomId(1L);
        room.setDescription("A Test description");
        room.setRoomType(RoomType.ONE_BED);
        room.setIsAvailable(true);
        room.setPricePerNight(BigDecimal.valueOf(240));
        room.setRoomNumber(1);

        when(roomService.updateRoom(eq(1L), any(RoomUpdateRequest.class), any(MultipartFile[].class)))
                .thenReturn(room);

        mockMvc.perform(
                        multipart("/api/rooms/1")
                                .file(img1)
                                .file(img2)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.description").value("A Test description"))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"))
                .andExpect(jsonPath("$.isAvailable").value(true))
                .andExpect(jsonPath("$.roomNumber").value(1));

        verify(roomService).updateRoom(eq(1L), any(RoomUpdateRequest.class), any(MultipartFile[].class));
    }

    // ---------------------------------------------------------
    // GET ROOM WITH IMAGES
    // ---------------------------------------------------------
    @Test
    void should_return_room_with_images_response() throws Exception {

        RoomResponse roomResponse = new RoomResponse(
                1L,
                RoomType.ONE_BED,
                true,
                "A Test description",
                BigDecimal.valueOf(240.00),
                1,
                LocalDateTime.now()
        );

        RoomImagesResponse img1 = new RoomImagesResponse(
                10L, 1L, "Room image", "img1.jpg", "url1", null
        );

        RoomImagesResponse img2 = new RoomImagesResponse(
                11L, 1L, "Room image", "img2.jpg", "url2", null
        );

        RoomWithImagesResponse response = new RoomWithImagesResponse(
                roomResponse,
                List.of(img1, img2)
        );

        when(roomService.findOrThrow(1L)).thenReturn(response);

        mockMvc.perform(get("/rooms/{roomId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room.roomId").value(1))
                .andExpect(jsonPath("$.room.roomType").value("ONE_BED"))
                .andExpect(jsonPath("$.room.isAvailable").value(true))
                .andExpect(jsonPath("$.room.description").value("A Test description"))
                .andExpect(jsonPath("$.room.roomNumber").value(1))
                .andExpect(jsonPath("$.images.length()").value(2))
                .andExpect(jsonPath("$.images[0].imageId").value(10))
                .andExpect(jsonPath("$.images[0].path").value("url1"))
                .andExpect(jsonPath("$.images[1].imageId").value(11))
                .andExpect(jsonPath("$.images[1].path").value("url2"));
    }

    // ---------------------------------------------------------
    // GET ALL ROOMS
    // ---------------------------------------------------------
    @Test
    void should_find_all_rooms() throws Exception {

        RoomResponse r1 = new RoomResponse(
                1L, RoomType.ONE_BED, true,
                "A Test description", BigDecimal.valueOf(240.00),
                2, LocalDateTime.now()
        );

        RoomResponse r2 = new RoomResponse(
                2L, RoomType.TWO_BED, false,
                "Another Test description", BigDecimal.valueOf(240.00),
                1, LocalDateTime.now()
        );

        RoomImagesResponse img1 = new RoomImagesResponse(
                10L, 1L, "Room image", "img1.jpg", "url1", null
        );

        RoomImagesResponse img2 = new RoomImagesResponse(
                11L, 1L, "Room image", "img2.jpg", "url2", null
        );

        RoomWithImagesResponse dto1 = new RoomWithImagesResponse(r1, List.of(img1, img2));
        RoomWithImagesResponse dto2 = new RoomWithImagesResponse(r2, List.of(img1, img2));

        when(roomService.findAllRooms()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/rooms/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---------------------------------------------------------
    // DELETE ROOM
    // ---------------------------------------------------------
    @Test
    void should_delete_a_room() throws Exception {

        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/rooms/{roomId}", 1L))
                .andExpect(status().isNoContent());

        verify(roomService).deleteRoom(1L);
    }
}
