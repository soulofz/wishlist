package com.followdream.service;

import com.followdream.exception.ForbiddenException;
import com.followdream.model.Security;
import com.followdream.model.User;
import com.followdream.model.enums.Role;
import com.followdream.repository.ItemRepository;
import com.followdream.repository.SecurityRepository;
import com.followdream.repository.UserRepository;
import com.followdream.repository.WishlistRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
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

    public Optional<User> getUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Security> security = securityRepository.findByUsername(username);
        if (security.isPresent()) {
            return Optional.of(security.get().getUser());
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
}
