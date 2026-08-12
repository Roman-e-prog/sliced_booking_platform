package com.roman.room_service.roomsetting.dto;

public class ImageUpdateRequest {
    private Long imageId;
    private String alt;
    private String title;

    public ImageUpdateRequest() {
    }

    public ImageUpdateRequest(Long imageId, String alt, String title) {
        this.imageId = imageId;
        this.alt = alt;
        this.title = title;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}


