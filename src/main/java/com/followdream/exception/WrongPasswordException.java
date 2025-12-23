package com.followdream.exception;

public class WrongPasswordException extends Exception {
    private String wrongPassword;

    public WrongPasswordException(String wrongPassword) {
        this.wrongPassword = wrongPassword;
    }

    @Override
    public String getMessage() {
        return "Wrong Password:" + wrongPassword;
    }
}
