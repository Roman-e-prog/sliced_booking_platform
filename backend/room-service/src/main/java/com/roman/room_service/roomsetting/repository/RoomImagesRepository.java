package com.roman.room_service.roomsetting.repository;
import com.roman.room_service.roomsetting.model.RoomImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomImagesRepository extends JpaRepository<RoomImages, Long> {
}
