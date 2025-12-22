package com.followdream.controller;

import com.followdream.exception.ForbiddenException;
import com.followdream.model.Security;
import com.followdream.model.User;
import com.followdream.model.dto.SecurityUpdateDto;
import com.followdream.model.dto.UserResponseDto;
import com.followdream.model.dto.UserUpdateDto;
import com.followdream.repository.SecurityRepository;
import com.followdream.repository.UserRepository;
import com.followdream.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/settings")
public class SettingsController {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SettingsController(UserRepository userRepository, SecurityRepository securityRepository, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @PutMapping("/security")
    public ResponseEntity<Security> updateSecurity(@Valid @RequestBody SecurityUpdateDto securityUpdateDto) throws ForbiddenException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> userSecurity = securityRepository.findByUsername(username);
        if (userSecurity.isEmpty()) {
            throw new ForbiddenException();
        }
        Security security = userSecurity.get();

        if (!bCryptPasswordEncoder.matches(securityUpdateDto.getCurrentPassword(), security.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        if (securityUpdateDto.getNewPassword() != null && !securityUpdateDto.getNewPassword().isBlank()) {
            if (bCryptPasswordEncoder.matches(securityUpdateDto.getNewPassword(), security.getPassword())) {
                return ResponseEntity.badRequest().build();
            }
            security.setPassword(bCryptPasswordEncoder.encode(securityUpdateDto.getNewPassword()));
        }
        if (securityUpdateDto.getEmail() != null && !securityUpdateDto.getEmail().isBlank()) {
            Optional<Security> existingSecurity = securityRepository.findByEmail(securityUpdateDto.getEmail());
            if (existingSecurity.isPresent() && !existingSecurity.get().getId().equals(security.getId())) {
                return ResponseEntity.badRequest().build();
            }
            security.setEmail(securityUpdateDto.getEmail());
        }
        Security savedSecurity = securityRepository.save(security);
        User user = savedSecurity.getUser();
        user.setUpdated(LocalDateTime.now());
        return ResponseEntity.ok(savedSecurity);
    }

}
