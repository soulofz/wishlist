package com.wishlist.controller;

import com.wishlist.exception.ForbiddenException;
import com.wishlist.exception.UserNotFoundException;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable("username") String username) throws ForbiddenException {
        Optional<UserResponseDto> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{username}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable String username) {
        User user = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(-1))
                .getUser();

        return userService.getAvatarResponse(user);
    }
}
