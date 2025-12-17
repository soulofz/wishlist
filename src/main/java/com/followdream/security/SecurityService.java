package com.followdream.security;

import com.followdream.exception.UsernameExistsException;
import com.followdream.exception.WrongPasswordException;
import com.followdream.model.Security;
import com.followdream.model.User;
import com.followdream.model.dto.AuthRequest;
import com.followdream.model.dto.UserRegistrationDto;
import com.followdream.model.enums.Role;
import com.followdream.repository.SecurityRepository;
import com.followdream.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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

    public SecurityService(UserRepository userRepository, SecurityRepository securityRepository, JwtUtils jwtUtils, BCryptPasswordEncoder bCryptPasswordEncoder) {
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

    public Optional<Security> getSecurityById(Long id) {
        return securityRepository.findById(id);
    }

    public Optional<Security> getSecurityByUsername(String username) {
        return securityRepository.findByUsername(username);
    }

    public List<Security> getAllSecurityByRole(String role) {
        return securityRepository.findByRole(role);
    }

    public Optional<String> generateJwt(AuthRequest request) throws WrongPasswordException {
        Optional<Security> security = securityRepository.findByUsername(request.getUsername());
        if(security.isEmpty()){
            throw new UsernameNotFoundException(request.getUsername());
        }
        if(!bCryptPasswordEncoder.matches(request.getPassword(), security.get().getPassword())){
            throw new WrongPasswordException(request.getPassword());
        }
        return Optional.ofNullable(jwtUtils.generateToken(security.get()));
    }

}
