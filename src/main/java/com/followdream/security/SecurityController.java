package com.followdream.security;

import com.followdream.exception.UsernameExistsException;
import com.followdream.exception.WrongPasswordException;
import com.followdream.model.Security;
import com.followdream.model.dto.AuthRequest;
import com.followdream.model.dto.AuthResponse;
import com.followdream.model.dto.UserRegistrationDto;
import com.followdream.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserService userService;
    private final SecurityService securityService;

    public SecurityController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
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
    public ResponseEntity<AuthResponse> generateJwtToken(@RequestBody AuthRequest authRequest) throws WrongPasswordException {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new ValidationException("Invalid request");
        }
        Optional<String> jwt = securityService.generateJwt(authRequest);
        if (jwt.isPresent()) {
            return new ResponseEntity<>(new AuthResponse(jwt.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Security> getSecurityById(@PathVariable("id") long id) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
}
