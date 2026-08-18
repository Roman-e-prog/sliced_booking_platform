package com.roman.user_service.user.model;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="users")
public class User {

    public enum Role {
        ADMIN,
        USER,
        GUEST }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(nullable = false) //annotation
    private String prename; //fieldname

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String street;

    @Column(name="house_number", nullable = false, length = 255)
    private String houseNumber;

    @Column(name = "postal_code", nullable = false)
    private Integer postalCode;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name="birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name="is_admin")
    private Boolean isAdmin;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

}
