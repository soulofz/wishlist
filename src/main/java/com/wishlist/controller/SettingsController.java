package com.wishlist.controller;

import com.wishlist.exception.ForbiddenException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.SecurityUpdateDto;
import com.wishlist.model.dto.UserUpdateDto;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/settings")
public class SettingsController {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    public SettingsController(UserRepository userRepository, SecurityRepository securityRepository, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    @PostMapping("/account/avatar")
    public ResponseEntity<Void> uploadAvatar(@RequestParam MultipartFile file) {
        userService.uploadAvatar(file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/account/avatar")
    public ResponseEntity<Void> deleteAvatar() {
        userService.deleteAvatar();
        return ResponseEntity.noContent().build();
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
        boolean updated = false;

        if (securityUpdateDto.getNewPassword() != null && !securityUpdateDto.getNewPassword().isBlank()) {
            if (bCryptPasswordEncoder.matches(securityUpdateDto.getNewPassword(), security.getPassword())) {
                return ResponseEntity.badRequest().build();
            }
            security.setPassword(bCryptPasswordEncoder.encode(securityUpdateDto.getNewPassword()));
            updated = true;
        }

        if (securityUpdateDto.getEmail() != null && !securityUpdateDto.getEmail().isBlank()) {
            Optional<Security> existingEmailSecurity = securityRepository.findByEmail(securityUpdateDto.getEmail());
            if (existingEmailSecurity.isPresent() && !existingEmailSecurity.get().getId().equals(security.getId())) {
                return ResponseEntity.badRequest().build();
            }
            security.setEmail(securityUpdateDto.getEmail());
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        Security savedSecurity = securityRepository.save(security);
        User user = savedSecurity.getUser();
        if (user != null) {
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);
        }
        return ResponseEntity.ok(savedSecurity);
    }

}
