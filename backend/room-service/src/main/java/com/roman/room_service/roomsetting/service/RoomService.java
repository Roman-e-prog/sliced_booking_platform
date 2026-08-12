package com.roman.room_service.roomsetting.service;

import com.roman.room_service.webClient.client.PriceClient;

import com.roman.room_service.roomsetting.dto.ImageUpdateRequest;
import com.roman.room_service.roomsetting.dto.RoomUpdateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import com.roman.room_service.roomsetting.mapper.RoomMapper;
import com.roman.room_service.roomsetting.mapper.RoomImagesMapper;
import com.roman.room_service.cloudinary.CloudinaryService;
import com.roman.room_service.exceptions.NotFoundException;
import com.roman.room_service.roomsetting.dto.RoomRequest;
import com.roman.room_service.roomsetting.dto.RoomWithImagesResponse;
import com.roman.room_service.roomsetting.model.Room;
import com.roman.room_service.roomsetting.model.RoomImages;
import com.roman.room_service.roomsetting.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;
@Service

public class RoomService {

    private final RoomRepository roomRepository;
    private final CloudinaryService cloudinaryService;
    private final PriceClient priceClient;
    public RoomService(RoomRepository roomRepository,
                       CloudinaryService cloudinaryService, PriceClient priceClient){
        this.roomRepository = roomRepository;
        this.cloudinaryService = cloudinaryService;

        this.priceClient = priceClient;
    }

    @Transactional(readOnly = true)
    public RoomWithImagesResponse findOrThrow(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(roomId, "Room"));

        return new RoomWithImagesResponse(
                RoomMapper.toResponse(room),
                RoomImagesMapper.toResponseList(room.getImages())
        );
    }
    @Transactional(readOnly = true)
    public List<RoomWithImagesResponse> findAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                .map(room -> new RoomWithImagesResponse(
                        RoomMapper.toResponse(room),
                        RoomImagesMapper.toResponseList(room.getImages()) // ← Session ist offen
                ))
                .toList();
    }

    @Transactional
    public Room createRoom(RoomRequest request) {
        System.out.println(request);
        Room room = new Room();
        String roomTypeString = request.getRoomType().name();//umwandeln des enums in string
        BigDecimal pricePerNight = Optional.ofNullable(priceClient.fetchPrice(roomTypeString))
                .orElse(request.getPricePerNight());

        room.setPricePerNight(pricePerNight);
        room.setRoomType(request.getRoomType());
        room.setDescription(request.getDescription());
        room.setPricePerNight(pricePerNight);
        room.setRoomNumber(request.getRoomNumber());

        List<RoomImages> images = new ArrayList<>();
        String[] alts = request.getAlts();
        MultipartFile[] files = request.getImages();
        System.out.println("alts length=" + (alts == null ? "null" : alts.length));
        System.out.println("files length=" + (files == null ? "null" : files.length));
        if (alts.length != files.length) {
            throw new IllegalArgumentException("Number of alts must match number of images");
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            @SuppressWarnings("unchecked")
            Map<String, Object> upload = (Map<String, Object>) cloudinaryService.upload(file);

            RoomImages img = new RoomImages();
            img.setRoom(room);
            img.setPath(upload.get("secure_url").toString());
            img.setTitle(upload.get("original_filename").toString());
            img.setAlt(alts[i]); // ← PERFECT MATCH
            img.setPublicId(upload.get("public_id").toString());

            images.add(img);
        }

        room.setImages(images);
        return roomRepository.save(room);

    }
    @Transactional
    public Room updateRoom(Long roomId, RoomUpdateRequest request, MultipartFile[] files) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(roomId, "Room"));

        // Alte Reihenfolge sichern
        List<RoomImages> oldOrder = new ArrayList<>(room.getImages());

        // Map für schnellen Zugriff
        Map<Long, RoomImages> existingImages = room.getImages().stream()
                .collect(Collectors.toMap(RoomImages::getImageId, img -> img));

        List<RoomImages> updatedImages = new ArrayList<>();
        int fileIndex = 0;

        for (ImageUpdateRequest meta : request.getImages()) {

            // Fall 1: bestehendes Bild behalten
            if (meta.getImageId() != null && (files == null || fileIndex >= files.length)) {
                RoomImages img = existingImages.get(meta.getImageId());
                img.setAlt(meta.getAlt());
                img.setTitle(meta.getTitle());
                updatedImages.add(img);
                continue;
            }

            // Fall 2: bestehendes Bild ersetzen
            if (meta.getImageId() != null && files != null && fileIndex < files.length) {

                RoomImages oldImg = existingImages.get(meta.getImageId());
                cloudinaryService.delete(oldImg.getPublicId());

                MultipartFile file = files[fileIndex++];

                Map<String, Object> upload = cloudinaryService.upload(file);

                RoomImages newImg = new RoomImages();
                newImg.setRoom(room);
                newImg.setPath(upload.get("secure_url").toString());
                newImg.setPublicId(upload.get("public_id").toString());
                newImg.setAlt(meta.getAlt());
                newImg.setTitle(meta.getTitle());

                updatedImages.add(newImg);
                continue;
            }

            // Fall 3: neues Bild hinzufügen
            if (meta.getImageId() == null && files != null && fileIndex < files.length) {

                MultipartFile file = files[fileIndex++];

                Map<String, Object> upload = cloudinaryService.upload(file);

                RoomImages img = new RoomImages();
                img.setRoom(room);
                img.setPath(upload.get("secure_url").toString());
                img.setPublicId(upload.get("public_id").toString());
                img.setAlt(meta.getAlt());
                img.setTitle(meta.getTitle());

                updatedImages.add(img);
                continue;
            }
        }

        // Jetzt Reihenfolge stabilisieren
        List<RoomImages> finalOrdered = new ArrayList<>();

        for (RoomImages oldImg : oldOrder) {
            // Bild existiert noch?
            RoomImages match = updatedImages.stream()
                    .filter(u -> u.getImageId() != null && u.getImageId().equals(oldImg.getImageId()))
                    .findFirst()
                    .orElse(null);

            if (match != null) {
                finalOrdered.add(match);
            }
        }

        // Neue Bilder (imageId == null) hinten anhängen
        updatedImages.stream()
                .filter(u -> u.getImageId() == null)
                .forEach(finalOrdered::add);

        room.getImages().clear();
        room.getImages().addAll(finalOrdered);

        room.setRoomType(request.getRoomType());
        room.setIsAvailable(request.getIsAvailable());
        room.setDescription(request.getDescription());
        room.setPricePerNight(request.getPricePerNight());
        room.setRoomNumber(request.getRoomNumber());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(roomId, "Room"));

        for (RoomImages img : room.getImages()) {
            cloudinaryService.delete(img.getPublicId());
        }

        roomRepository.delete(room);
    }


}

