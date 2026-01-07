package com.wishlist.controller;

import com.wishlist.model.dto.SecurityUpdateDto;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.dto.UserRequestDto;
import com.wishlist.security.SecurityService;
import com.wishlist.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final SecurityService securityService;

    public SettingsController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDto> updateAccount(@Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateAccount(dto));
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

    @GetMapping("/account/avatar")
    public ResponseEntity<Resource> getMyAvatar() {
        return userService.getAvatar(userService.getCurrentUser());
    }

    @PutMapping("/security")
    public ResponseEntity<Void> updateSecurity(@Valid @RequestBody SecurityUpdateDto dto) {
        securityService.updateSecurity(dto);
        return ResponseEntity.noContent().build();
    }
}
