package com.wishlist.service;

import com.wishlist.exception.AvatarUploadException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.dto.UserUpdateDto;
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

    @Value("${app.storage.root}")
    private String storageRoot;

    @Value("${app.storage.avatars}")
    private String avatarsDir;

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;

    public UserService(UserRepository userRepository,
                       SecurityService securityService,
                       SecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.securityRepository = securityRepository;
    }

    public User getCurrentUser() {
        return securityService.getCurrentSecurity().getUser();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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

    public User getUserByUsername(String username) {
        Security security = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return security.getUser();
    }


    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public UserResponseDto updateAccount(UserUpdateDto dto) {
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

        userRepository.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void uploadAvatar(MultipartFile file) {

        User user = getCurrentUser();
        String username = user.getSecurity().getUsername();

        validateAvatarFile(file);

        String extension = getExtensionFromContentType(file.getContentType());

        try {
            Path userAvatarDir = Paths.get(storageRoot, avatarsDir, username);
            Files.createDirectories(userAvatarDir);

            try (var paths = Files.list(userAvatarDir)) {
                paths.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        log.warn("Failed to delete old avatar file: {}", path, e);
                    }
                });
            }

            Path avatarPath = userAvatarDir.resolve("avatar" + extension);
            Files.copy(file.getInputStream(), avatarPath, StandardCopyOption.REPLACE_EXISTING);

            user.setAvatarPath(Paths.get(avatarsDir, username, "avatar" + extension).toString());

            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AvatarUploadException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AvatarUploadException("Only image files allowed");
        }

        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new AvatarUploadException("File is too large");
        }
    }

    private String getExtensionFromContentType(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                throw new AvatarUploadException("GIF is not allowed");
            case "image/webp":
                return ".webp";
            case "image/bmp":
                return ".bmp";
            case "image/svg+xml":
                throw new AvatarUploadException("SVG is not allowed");
            default:
                return ".jpg";
        }
    }

    @Transactional
    public void deleteAvatar() {
        User user = getCurrentUser();

        if (user.getAvatarPath() == null || user.getAvatarPath().isEmpty()) {
            return;
        }

        try {
            Path avatarPath = Paths.get(storageRoot).resolve(user.getAvatarPath());
            Files.deleteIfExists(avatarPath);

            user.setAvatarPath(null);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete avatar", e);
        }
    }

    public ResponseEntity<Resource> getAvatarResponse(User user) {
        if (user.getAvatarPath() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(storageRoot).resolve(user.getAvatarPath());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);

        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}