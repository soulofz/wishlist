package com.followdream.exception;

public class UsernameExistsException extends Exception {
    private String username;

    public UsernameExistsException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User with this username already exists";
    }
}