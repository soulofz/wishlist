package com.wishlist.security;

import com.wishlist.exception.ForbiddenException;
import com.wishlist.exception.UserNotFoundException;
import com.wishlist.exception.UsernameExistsException;
import com.wishlist.exception.WrongPasswordException;
import com.wishlist.model.Security;
import com.wishlist.model.User;
import com.wishlist.model.dto.AuthRequest;
import com.wishlist.model.dto.SecurityUpdateDto;
import com.wishlist.model.dto.UserRegistrationDto;
import com.wishlist.model.enums.Role;
import com.wishlist.repository.SecurityRepository;
import com.wishlist.repository.UserRepository;
import com.wishlist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityService(UserRepository userRepository,
                           SecurityRepository securityRepository,
                           JwtUtils jwtUtils,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.jwtUtils = jwtUtils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional(rollbackFor = {Exception.class},
            noRollbackFor = {UsernameExistsException.class},
            isolation = Isolation.READ_COMMITTED)

    public boolean isUsernameUsed(String username) {
        return securityRepository.existsByUsername(username);
    }

    public boolean registration(UserRegistrationDto userRegistrationDto) throws UsernameExistsException {
        log.info("Registering user {}", userRegistrationDto.getUsername());
        if (isUsernameUsed(userRegistrationDto.getUsername())) {
            throw new UsernameExistsException(userRegistrationDto.getUsername());
        }
        try {
            User user = new User();
            user.setAge(userRegistrationDto.getAge());
            user.setBirthday(userRegistrationDto.getBirthday());
            user.setCreated(LocalDateTime.now());
            user.setUpdated(LocalDateTime.now());
            userRepository.save(user);

            Security security = new Security();
            security.setUsername(userRegistrationDto.getUsername());
            security.setPassword(bCryptPasswordEncoder.encode(userRegistrationDto.getPassword()));
            security.setEmail(userRegistrationDto.getEmail());
            security.setUser(user);
            security.setRole(Role.USER);
            securityRepository.save(security);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public Security getCurrentSecurity() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(-1));
    }

    public Optional<Security> getSecurityById(Long id) {
        return securityRepository.findById(id);
    }

    public Optional<Security> getSecurityByUsername(String username) {
        return securityRepository.findByUsername(username);
    }

    @Transactional
    public void updateSecurity(SecurityUpdateDto dto) {
        Security security = getCurrentSecurity();

        if (!bCryptPasswordEncoder.matches(dto.getCurrentPassword(), security.getPassword())) {
            throw new IllegalArgumentException("Wrong current password");
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (bCryptPasswordEncoder.matches(dto.getNewPassword(), security.getPassword())) {
                throw new IllegalArgumentException("New password must be different");
            }
            security.setPassword(bCryptPasswordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            securityRepository.findByEmail(dto.getEmail())
                    .filter(s -> !s.getId().equals(security.getId()))
                    .ifPresent(s -> {
                        throw new IllegalArgumentException("Email already in use");
                    });
            security.setEmail(dto.getEmail());
        }

        securityRepository.save(security);
    }

    public List<Security> getAllSecurityByRole(Role role) {
        return securityRepository.findByRole(role.name());
    }

    public List<Security> getAllAdmins() {
        return getAllSecurityByRole(Role.ADMIN);
    }

    public List<Security> getAllModerators() {
        return getAllSecurityByRole(Role.MODERATOR);
    }

    public List<Security> getAllUsers() {
        return getAllSecurityByRole(Role.USER);
    }

    public Optional<String> generateJwt(AuthRequest request) throws WrongPasswordException {
        Optional<Security> security = securityRepository.findByUsername(request.getUsername());
        if (security.isEmpty()) {
            throw new UsernameNotFoundException(request.getUsername());
        }
        if (!bCryptPasswordEncoder.matches(request.getPassword(), security.get().getPassword())) {
            throw new WrongPasswordException(request.getPassword());
        }
        return Optional.ofNullable(jwtUtils.generateToken(security.get()));
    }

    private void ensureRoleChangeAllowed(
            Long actorSecurityId,
            Long targetSecurityId,
            Role actorRole,
            Role targetRole,
            Role newRole
    ) {
        if (actorSecurityId.equals(targetSecurityId)) {
            throw new AccessDeniedException("You cannot change your own role");
        }

        if (targetRole == null) {
            throw new IllegalStateException("Target user role is undefined");
        }

        if (targetRole.getLevel() >= actorRole.getLevel()) {
            throw new AccessDeniedException("You have no permission to modify this user");
        }

        if (newRole.getLevel() >= actorRole.getLevel()) {
            throw new AccessDeniedException("You cannot assign this role");
        }

        if (targetRole == Role.OWNER && actorRole != Role.OWNER) {
            throw new AccessDeniedException("Only OWNER may modify OWNER accounts");
        }
    }

    public boolean updateRoleById(Long securityId, Role role) {

        if (!securityRepository.existsById(securityId)) {
            throw new UserNotFoundException(securityId);
        }

        Security actorSecurity = getCurrentSecurity();

        Role actorRole = actorSecurity.getRole();
        Role targetRole = securityRepository.getRoleById(securityId);

        ensureRoleChangeAllowed(
                actorSecurity.getId(),
                securityId,
                actorRole,
                targetRole,
                role);

        return (securityRepository.updateRoleById(securityId, role.name())) > 0;
    }

    public boolean setAdmin(Long securityId) {
        return updateRoleById(securityId, Role.ADMIN);
    }

    public boolean setModerator(Long securityId) {
        return updateRoleById(securityId, Role.MODERATOR);
    }

    public boolean setUser(Long securityId) {
        return updateRoleById(securityId, Role.USER);
    }
}