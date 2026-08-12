package com.roman.room_service.roomsetting.service;

import com.roman.room_service.cloudinary.CloudinaryService;
import com.roman.room_service.enums.RoomType;
import com.roman.room_service.webClient.client.PriceClient;
import com.roman.room_service.roomsetting.dto.ImageUpdateRequest;
import com.roman.room_service.roomsetting.dto.RoomRequest;
import com.roman.room_service.roomsetting.dto.RoomUpdateRequest;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.model.RoomImages;
import com.roman.room_service.roomsetting.repository.RoomImagesRepository;
import com.roman.room_service.roomsetting.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomSettingServiceTest {

    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final RoomImagesRepository roomImagesRepository = mock(RoomImagesRepository.class);
    private final CloudinaryService cloudinaryService = mock(CloudinaryService.class);
    private final PriceClient priceClient = mock(PriceClient.class);
    private final RoomService roomService = new RoomService(
            roomRepository,
            cloudinaryService,
            priceClient);

    @Test
    void should_create_a_room() {

        MockMultipartFile img1 = new MockMultipartFile(
                "file", "test1.jpg", "image/jpeg", "dummy".getBytes()
        );
        MockMultipartFile img2 = new MockMultipartFile(
                "file", "test2.jpg", "image/jpeg", "dummy".getBytes()
        );

        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setDescription("A Test description");
        roomRequest.setRoomType(RoomType.ONE_BED);
        roomRequest.setIsAvailable(true);
        roomRequest.setImages(new MultipartFile[]{img1, img2});
        roomRequest.setAlts(new String[]{"Room image", "Room image"}); // ✔ korrigiert: Alt-Texte gesetzt

        Map<String, Object> upload1 = Map.of(
                "secure_url", "https://cloudinary.com/fake1.jpg",
                "original_filename", "test1.jpg",
                "public_id", "public1"
        );

        Map<String, Object> upload2 = Map.of(
                "secure_url", "https://cloudinary.com/fake2.jpg",
                "original_filename", "test2.jpg",
                "public_id", "public2"
        );

        when(cloudinaryService.upload(any(MultipartFile.class)))
                .thenReturn(upload1)
                .thenReturn(upload2);

        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Room result = roomService.createRoom(roomRequest);

        assertNotNull(result);
        assertEquals("A Test description", result.getDescription());
        assertEquals(RoomType.ONE_BED, result.getRoomType()); // ✔ korrigiert: Enum statt String

        assertEquals(2, result.getImages().size());

        RoomImages first = result.getImages().get(0);
        RoomImages second = result.getImages().get(1);

        assertEquals("https://cloudinary.com/fake1.jpg", first.getPath());
        assertEquals("test1.jpg", first.getTitle());
        assertEquals("Room image", first.getAlt()); // ✔ korrekt, da Alt aus Request kommt
        assertEquals("public1", first.getPublicId());

        assertEquals("https://cloudinary.com/fake2.jpg", second.getPath());
        assertEquals("test2.jpg", second.getTitle());
        assertEquals("Room image", second.getAlt()); // ✔ korrekt
        assertEquals("public2", second.getPublicId());

        verify(cloudinaryService, times(2)).upload(any(MultipartFile.class));
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void should_update_a_room() {

        // --- Arrange ---

        // Dateien für neue/ersetzte Bilder
        MockMultipartFile img1 = new MockMultipartFile(
                "images", "test1.jpg", "image/jpeg", "dummy".getBytes()
        );
        MockMultipartFile img2 = new MockMultipartFile(
                "images", "test2.jpg", "image/jpeg", "dummy".getBytes()
        );

        // Bestehender Room
        Room room = new Room();
        room.setRoomId(1L);
        room.setDescription("Old description");
        room.setRoomType(RoomType.ONE_BED);
        room.setRoomNumber(1);
        room.setIsAvailable(true);

        // Bestehende Bilder
        RoomImages ri1 = new RoomImages();
        ri1.setImageId(10L);
        ri1.setPublicId("existing-public-id-1");
        ri1.setRoom(room);

        RoomImages ri2 = new RoomImages();
        ri2.setImageId(11L);
        ri2.setPublicId("existing-public-id-2");
        ri2.setRoom(room);

        room.setImages(new ArrayList<>(List.of(ri1, ri2)));

        // Update-Request (Metadaten)
        RoomUpdateRequest updateRequest = new RoomUpdateRequest();
        updateRequest.setDescription("A Test description");
        updateRequest.setRoomType(RoomType.ONE_BED);
        updateRequest.setIsAvailable(true);
        updateRequest.setPricePerNight(BigDecimal.valueOf(200));
        updateRequest.setRoomNumber(1);

        updateRequest.setImages(List.of(
                new ImageUpdateRequest(10L, "Alt 1", "Title 1"), // ersetzt
                new ImageUpdateRequest(11L, "Alt 2", "Title 2")  // ersetzt
        ));

        // Cloudinary Upload Mock
        Map<String, Object> upload1 = Map.of(
                "secure_url", "https://cloudinary.com/fake1.jpg",
                "original_filename", "test1.jpg",
                "public_id", "public1"
        );

        Map<String, Object> upload2 = Map.of(
                "secure_url", "https://cloudinary.com/fake2.jpg",
                "original_filename", "test2.jpg",
                "public_id", "public2"
        );

        when(cloudinaryService.upload(any(MultipartFile.class)))
                .thenReturn(upload1)
                .thenReturn(upload2);

        doNothing().when(cloudinaryService).delete(anyString());

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        Room result = roomService.updateRoom(1L, updateRequest, new MultipartFile[]{img1, img2});

        // --- Assert ---
        assertNotNull(result);
        assertEquals("A Test description", result.getDescription());
        assertEquals(RoomType.ONE_BED, result.getRoomType());
        assertTrue(result.getIsAvailable());
        assertEquals(1, result.getRoomNumber());

        // Alte Bilder müssen gelöscht werden
        verify(cloudinaryService).delete("existing-public-id-1");
        verify(cloudinaryService).delete("existing-public-id-2");

        // Neue Bilder müssen hochgeladen werden
        verify(cloudinaryService, times(2)).upload(any(MultipartFile.class));

        // Neue Bilder müssen im Room sein
        assertEquals(2, result.getImages().size());

        RoomImages new1 = result.getImages().get(0);
        RoomImages new2 = result.getImages().get(1);

        assertEquals("https://cloudinary.com/fake1.jpg", new1.getPath());
        assertEquals("public1", new1.getPublicId());
        assertEquals("Alt 1", new1.getAlt());
        assertEquals("Title 1", new1.getTitle());

        assertEquals("https://cloudinary.com/fake2.jpg", new2.getPath());
        assertEquals("public2", new2.getPublicId());
        assertEquals("Alt 2", new2.getAlt());
        assertEquals("Title 2", new2.getTitle());
    }



    @Test
    void should_delete_the_room() {
        Room existing = new Room();
        existing.setRoomId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));

        roomService.deleteRoom(existing.getRoomId());

        verify(roomRepository).delete(existing);
    }
}
