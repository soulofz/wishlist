package com.wishlist.exception;

public class FriendRequestNotFoundException extends Exception {

    public FriendRequestNotFoundException(Long senderId, Long receiverId) {
        super("Friend request not found between senderId=" + senderId + " and receiverId=" + receiverId);
    }
}
