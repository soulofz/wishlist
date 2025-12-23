package com.wishlist.exception;


import com.wishlist.model.User;

public class UserNotFoundException extends RuntimeException {
    private final User user;

    public UserNotFoundException(User user) {
        super();
        this.user = user;
    }

    public UserNotFoundException(long id) {
        super();
        User user = new User();
        user.setId(id);
        this.user = user;
    }


    @Override
    public String toString() {
        return "UserNotFoundException{user with id = " + user.getId() + " not found}";
    }
}
