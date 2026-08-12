package com.roman.booking_service.exceptions;

public class InvalidException extends RuntimeException{
    public InvalidException(String message) {
        super(message);
    }
}
