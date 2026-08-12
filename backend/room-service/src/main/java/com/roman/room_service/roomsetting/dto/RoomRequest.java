package com.roman.room_service.roomsetting.dto;

import com.roman.room_service.enums.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class RoomRequest {
    private RoomType roomType;
    private Boolean isAvailable;
    private String description;
    private BigDecimal pricePerNight;
    private Integer roomNumber;
    private MultipartFile[] images;
    private String[] alts;


    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }

    public String[] getAlts() {
        return alts;
    }

    public void setAlts(String[] alts) {
        this.alts = alts;
    }
}
