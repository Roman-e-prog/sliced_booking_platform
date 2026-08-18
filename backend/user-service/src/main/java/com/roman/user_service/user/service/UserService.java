package com.roman.user_service.user.service;


import com.roman.user_service.events.UserDeletedEvent;
import com.roman.user_service.exceptions.NotFoundException;
import com.roman.user_service.user.dto.UserRequest;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.roman.user_service.events.EventPublisher;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final EventPublisher eventPublisher;
        public UserService(
                UserRepository userRepository,
                PasswordEncoder passwordEncoder,
                EventPublisher eventPublisher){
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.eventPublisher = eventPublisher;
        }
            public User findOrThrow(Long userId){
                return userRepository.findById(userId)
                        .orElseThrow(()->new NotFoundException(userId, "User"));
            }
            public List<User> findAll(){
                return userRepository.findAll();
            }

            public User updateUser(Long userId, UserRequest userRequest){
                LocalDateTime currentDateTime = LocalDateTime.now();
                User user = findOrThrow(userId);
                user.setPrename(userRequest.prename);
                user.setLastname(userRequest.lastname);
                user.setStreet(userRequest.street);
                user.setHouseNumber(userRequest.houseNumber);
                user.setPostalCode(userRequest.postalCode);
                user.setTown(userRequest.town);
                user.setCountry(userRequest.country);
                user.setPassword(passwordEncoder.encode(userRequest.password));
                user.setUpdatedAt(currentDateTime);

                return userRepository.save(user);
            }
            public void deleteUser(Long userId){
                User user = findOrThrow(userId);
                userRepository.delete(user);
                eventPublisher.publish(new UserDeletedEvent(userId));
            }
}