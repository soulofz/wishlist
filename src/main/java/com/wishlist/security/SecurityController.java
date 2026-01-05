package com.wishlist.security;

import com.wishlist.exception.UsernameExistsException;
import com.wishlist.exception.WrongPasswordException;
import com.wishlist.model.Security;
import com.wishlist.model.dto.AuthRequest;
import com.wishlist.model.dto.AuthResponse;
import com.wishlist.model.dto.UserRegistrationDto;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;
    private final JwtUtils jwtUtils;

    public SecurityController(SecurityService securityService, JwtUtils jwtUtils) {
        this.securityService = securityService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatusCode> registration(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
                                                       BindingResult bindingResult) throws UsernameExistsException {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                log.warn(objectError.toString());
                errorMessages.add(objectError.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errorMessages));
        }
        if (securityService.registration(userRegistrationDto)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/jwt")
    public ResponseEntity<AuthResponse> generateJwtToken(@Valid @RequestBody AuthRequest authRequest) throws WrongPasswordException {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new ValidationException("Invalid request");
        }
        String accessToken = securityService.generateAccessToken(authRequest);
        String refreshToken = securityService.generateRefreshToken(authRequest);

        if (accessToken != null && refreshToken != null) {
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken) {
        try {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            Security security = securityService.getSecurityByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));

            if (security != null && jwtUtils.validateToken(refreshToken)) {
                String newAccessToken = jwtUtils.generateToken(security);
                return ResponseEntity.ok(newAccessToken);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");

        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        } catch (Exception e) {
            log.error("Error while refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while refreshing token");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/{securityId}")
    public ResponseEntity<Security> getSecurityById(@PathVariable("securityId") long id) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/admins")
    public ResponseEntity<List<Security>> getAdmins() {
        return ResponseEntity.ok(securityService.getAllAdmins());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/moderators")
    public ResponseEntity<List<Security>> getModerators() {
        return ResponseEntity.ok(securityService.getAllModerators());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/users")
    public ResponseEntity<List<Security>> getUsers() {
        return ResponseEntity.ok(securityService.getAllUsers());
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{securityId}/makeAdmin")
    public ResponseEntity<HttpStatusCode> makeAdminById(@PathVariable("securityId") Long securityId) {
        if (securityService.setAdmin(securityId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping("/{securityId}/makeModerator")
    public ResponseEntity<HttpStatusCode> makeModeratorById(@PathVariable("securityId") Long securityId) {
        if (securityService.setModerator(securityId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping("/{securityId}/makeUser")
    public ResponseEntity<HttpStatusCode> makeUserById(@PathVariable("securityId") Long securityId) {
        if (securityService.setUser(securityId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
