package com.roman.room_service.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(Long id, String element) {
        super(element +" with ID " + id + " not found");
    }
}
