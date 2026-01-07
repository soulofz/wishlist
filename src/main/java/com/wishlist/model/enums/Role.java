package com.wishlist.model.enums;

public enum Role {
    USER(0), MODERATOR(1), ADMIN(2), OWNER(3);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
