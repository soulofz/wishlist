package com.followdream.controller;

import com.followdream.exception.ForbiddenException;
import com.followdream.model.Security;
import com.followdream.model.User;
import com.followdream.model.dto.UserResponseDto;
import com.followdream.model.dto.UserUpdateDto;
import com.followdream.repository.SecurityRepository;
import com.followdream.repository.UserRepository;
import com.followdream.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final UserService userService;

    public SettingsController(UserRepository userRepository, SecurityRepository securityRepository, UserService userService) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.userService = userService;
    }

    @PutMapping("/account")
    public ResponseEntity<User> updateAccount(@Valid @RequestBody UserUpdateDto userUpdateDto) throws ForbiddenException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> userSecurity = securityRepository.findByUsername(username);
        if (userSecurity.isEmpty()) {
            throw new ForbiddenException();
        }
        User user = userSecurity.get().getUser();
        if (userUpdateDto.getFirstName() != null && !userUpdateDto.getFirstName().isBlank()) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null && !userUpdateDto.getLastName().isBlank()) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getBirthday() != null) {
            if (userUpdateDto.getBirthday().isAfter(LocalDate.now())) {
                return ResponseEntity.badRequest().build();
            }
            user.setBirthday(userUpdateDto.getBirthday());
        }
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

}
