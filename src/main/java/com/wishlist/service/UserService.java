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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private static final String AVATARS_DIR = "avatars";

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
        Optional<Security> security = securityRepository.findById(user.getId());
        userResponseDto.setUsername(security.get().getUsername());
        userResponseDto.setBirthday(user.getBirthday());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setAge(user.getAge());
        if (user.getAvatarPath() != null) {
            userResponseDto.setAvatar(new File(user.getAvatarPath()));
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

    public Optional<User> updateUser(User user) throws ForbiddenException {
        Optional<User> userFromDbOptional = getUserById(user.getId());
        if (userFromDbOptional.isPresent()) {
            return Optional.of(userRepository.saveAndFlush(user));
        } else {
            throw new UserNotFoundException(user);
        }
    }

    public void uploadAvatar(MultipartFile file) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Security> userSecurity = securityRepository.findByUsername(username);
            if (userSecurity.isEmpty()) {
                throw new UsernameNotFoundException(username);
            }
            Path avatarRoot = Paths.get(AVATARS_DIR);
            if (!Files.exists(avatarRoot)) {
                Files.createDirectories(avatarRoot);
            }
            Path userDir = avatarRoot.resolve(username);
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }
            Path avatarPath = userDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), avatarPath, StandardCopyOption.REPLACE_EXISTING);

            User user = userSecurity.get().getUser();
            user.setAvatarPath(avatarPath.toString());
            userRepository.save(user);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }
}
