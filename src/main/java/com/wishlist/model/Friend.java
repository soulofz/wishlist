package com.wishlist.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {

    @EmbeddedId
    private FriendKey id;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    public Friend(Long userId, Long friendId) {
        this.id = new FriendKey(userId, friendId);
        this.created = LocalDateTime.now();
    }

    public Friend(FriendKey id) {
        this.id = id;
        this.created = LocalDateTime.now();
    }
}
