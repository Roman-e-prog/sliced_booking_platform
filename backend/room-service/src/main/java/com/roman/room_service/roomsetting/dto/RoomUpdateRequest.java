package com.roman.room_service.roomsetting.dto;

import com.roman.room_service.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;

public class RoomUpdateRequest {
    private RoomType roomType;
    private Boolean isAvailable;
    private String description;
    private BigDecimal pricePerNight;
    private Integer roomNumber;
    private List<ImageUpdateRequest> images;

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

    public List<ImageUpdateRequest> getImages() {
        return images;
    }

    public void setImages(List<ImageUpdateRequest> images) {
        this.images = images;
    }
}


