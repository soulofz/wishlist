package com.wishlist.service;

import com.wishlist.exception.AvatarUploadException;
import com.wishlist.exception.ForbiddenException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.dto.UserUpdateDto;
import com.wishlist.model.enums.Role;
import com.wishlist.repository.ItemRepository;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.repository.WishlistRepository;
import com.wishlist.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final SecurityService securityService;
    @Value("${app.storage.root}")
    private String storageRoot;

    @Value("${app.storage.avatars}")
    private String avatarsDir;

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final WishlistRepository wishlistRepository;
    private final ItemRepository itemRepository;

    public UserService(UserRepository userRepository, SecurityRepository securityRepository, WishlistRepository wishlistRepository, ItemRepository itemRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.wishlistRepository = wishlistRepository;
        this.itemRepository = itemRepository;
        this.securityService = securityService;
    }

    private User getCurrentUser() {
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


    public Optional<UserResponseDto> getUserByUsername(String username) throws UsernameNotFoundException {
        User user = getCurrentUser();
        UserResponseDto userResponseDto = convertToDto(user);
        return Optional.of(userResponseDto);
    }


    public Optional<User> getUserById(long id) throws ForbiddenException {
        User currentUser = getCurrentUser();
        if (currentUser.getSecurity().getRole().equals(Role.ADMIN)) {
            return userRepository.findById(id);
        }
        throw new ForbiddenException();
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

}