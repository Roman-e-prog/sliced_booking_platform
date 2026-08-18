package com.roman.user_service.user.controller;

import com.roman.user_service.user.dto.UserRequest;
import com.roman.user_service.user.dto.UserResponse;
import com.roman.user_service.user.mapper.UserMapper;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @PreAuthorize("@userSecurity.isOwner(#userId)")
    @PutMapping("/{userId}")
        public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid
            @RequestBody UserRequest userRequest
            ){
        User updated = userService.updateUser(userId, userRequest);

        UserResponse userResponse = UserMapper.toResponse(updated);
        return ResponseEntity.ok(userResponse);
    }
    @PreAuthorize("@userSecurity.isOwner(#userId) or hasRole('Admin')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getSingleUser(
            @PathVariable Long userId){
        User user = userService.findOrThrow(userId);
        UserResponse userResponse = UserMapper.toResponse(user);
        return ResponseEntity.ok(userResponse);
    }
    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUser(){
        List<User> users = userService.findAll();
        List<UserResponse> userResponses = users.stream().map(UserMapper::toResponse).toList();
        return ResponseEntity.ok(userResponses);
    }
    @PreAuthorize("@userSecurity.isOwner('#userId) or hasRole('Admin')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
