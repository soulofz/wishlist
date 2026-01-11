package com.wishlist.controller;

import com.wishlist.model.dto.SecurityUpdateDto;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.dto.UserRequestDto;
import com.wishlist.security.SecurityService;
import com.wishlist.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<UserResponseDto> updateAccount(@Valid @ModelAttribute UserRequestDto dto , MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.updateAccount(dto,file));
    }

    @PutMapping("/security")
    public ResponseEntity<Void> updateSecurity(@Valid @RequestBody SecurityUpdateDto dto) {
        securityService.updateSecurity(dto);
        return ResponseEntity.noContent().build();
    }
}
