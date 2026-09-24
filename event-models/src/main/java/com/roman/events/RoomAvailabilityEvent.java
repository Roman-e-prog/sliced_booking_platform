package com.roman.events;

public record RoomAvailabilityEvent(Integer roomNumber, boolean isAvailable) {
}
