package com.wishlist.controller;

import com.wishlist.exception.UserNotFoundException;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${app.storage.root}")
    private String storageRoot;

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, SecurityRepository securityRepository, UserService userService) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<UserResponseDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(userService.convertToDto(user));
        }

        return ResponseEntity.ok(usersDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(userService.convertToDto(user.get()));
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        UserResponseDto userResponseDto = userService.convertToDto(user);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/{username}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable String username) {
        User user = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(-1))
                .getUser();

        return userService.getAvatar(user);
    }
}
