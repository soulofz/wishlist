package com.wishlist.service;

import com.wishlist.exception.ForbiddenException;
import com.wishlist.exception.UserNotFoundException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.UserResponseDto;
import com.wishlist.model.enums.Role;
import com.wishlist.repository.ItemRepository;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.repository.WishlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Value("${app.storage.root}")
    private String storageRoot;

    @Value("${app.storage.avatars}")
    private String avatarsDir;

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final WishlistRepository wishlistRepository;
    private final ItemRepository itemRepository;

    public UserService(UserRepository userRepository, SecurityRepository securityRepository, WishlistRepository wishlistRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.wishlistRepository = wishlistRepository;
        this.itemRepository = itemRepository;
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
        Optional<Security> security = securityRepository.findByUsername(username);
        if (security.isPresent()) {
            User user = security.get().getUser();
            UserResponseDto userResponseDto = convertToDto(user);
            return Optional.of(userResponseDto);
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public Optional<User> getUserById(long id) throws ForbiddenException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> userSecurity = securityRepository.findByUsername(username);
        if (userSecurity.isPresent() && userSecurity.get().getRole().equals(Role.ADMIN)) {
            return userRepository.findById(id);
        } else {
            throw new ForbiddenException();
        }
    }

    public void uploadAvatar(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Security security = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(-1));
        User user = security.getUser();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large");
        }

        String extension = getExtensionFromContentType(contentType);

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

    private String getExtensionFromContentType(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                throw new IllegalArgumentException("GIF is not allowed");
            case "image/webp":
                return ".webp";
            case "image/bmp":
                return ".bmp";
            case "image/svg+xml":
                throw new IllegalArgumentException("SVG is not allowed");
            default:
                return ".jpg";
        }
    }

    @Transactional
    public void deleteAvatar() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Security security = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(-1));
        User user = security.getUser();

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