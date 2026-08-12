package com.roman.user_service.apiRequest;


import com.roman.user_service.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.roman.user_service.user.model.User;
@RestController
@RequestMapping("/userService")
public class ApiUserController {
    private final UserRepository userRepository;

    public ApiUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> fetchUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
