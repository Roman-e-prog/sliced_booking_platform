package com.roman.user_service.user.dto;

import java.time.LocalDate;

public class UserRequest {
    public Long userId;
    public String prename;
    public String lastname;
    public String username;
    public String street;
    public String houseNumber;
    public Integer postalCode;
    public String town;
    public String country;
    public String email;
    public LocalDate birthDate;
    public String password;
}
