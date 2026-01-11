package com.wishlist.service;

import com.wishlist.exception.AvatarUploadException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.dto.UserRequestDto;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    private final CloudImageService cloudImageService;

    @Value("${app.storage.root}")
    private String storageRoot;

    @Value("${app.storage.avatars}")
    private String avatarsDir;

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;

    public UserService(UserRepository userRepository,
                       SecurityService securityService,
                       SecurityRepository securityRepository, CloudImageService cloudImageService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.securityRepository = securityRepository;
        this.cloudImageService = cloudImageService;
    }

    public User getCurrentUser() {
        return securityService.getCurrentSecurity().getUser();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        Security security = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return security.getUser();
    }

    public UserResponseDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto userResponseDto = new UserResponseDto();
        Security security = user.getSecurity();

        userResponseDto.setUsername(security.getUsername());
        userResponseDto.setBirthday(user.getBirthday());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setAge(user.getAge());

        if (user.getAvatarPath() != null) {
            userResponseDto.setAvatarUrl("/" + user.getAvatarPath());
        }
        return userResponseDto;
    }

    @Transactional
    public UserResponseDto updateAccount(UserRequestDto dto, MultipartFile file) throws IOException {
        User user = getCurrentUser();

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getBirthday() != null) {
            if (dto.getBirthday().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Birthday cannot be in the future");
            }
            user.setBirthday(dto.getBirthday());
        }
        if (file != null && !file.isEmpty()) {
            validateAvatarFile(file);
            if (user.getAvatarPublicId() != null){
                cloudImageService.deleteImage(user.getAvatarPublicId());
            }
            CloudImageService.CloudImageUploadResult uploadResult = cloudImageService.uploadImage(file,"users");
            user.setAvatarPath(uploadResult.imageUrl());
            user.setAvatarPublicId(uploadResult.publicId());
        }

        userRepository.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(){
        Security security = securityService.getCurrentSecurity();
        User user = getCurrentUser();

        if (user.getAvatarPublicId() != null){
            cloudImageService.deleteImage(user.getAvatarPublicId());
        }

        userRepository.delete(user);
        securityRepository.delete(security);
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Avatar file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files allowed");
        }

        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new RuntimeException("Avatar file is too large");
        }
    }
}