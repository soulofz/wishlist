package com.wishlist.exception;

import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<HttpStatusCode> usernameExistsException(UsernameExistsException e) {
        log.warn("Username exists: {}", e.getUsername());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<HttpStatusCode> sqlException(SQLException e) {
        log.warn("SQL exception: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpStatusCode> userNotFoundException(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<HttpStatusCode> validationException(ValidationException e) {
        log.warn("Validation exception: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<HttpStatusCode> forbiddenException(ForbiddenException e) {
        log.warn("Forbidden exception: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpStatusCode> usernameNotFoundException(UsernameNotFoundException e) {
        log.warn("Username not found: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<HttpStatusCode> wrongPasswordException(WrongPasswordException e) {
        log.warn("Wrong password: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AvatarUploadException.class)
    public ResponseEntity<HttpStatusCode> wrongPasswordException(AvatarUploadException e) {
        log.warn("Avatar Upload Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FriendRequestNotFoundException.class)
    public ResponseEntity<HttpStatusCode> friendRequestNotFoundException(FriendRequestNotFoundException e) {
        log.warn("Friend request not found: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}