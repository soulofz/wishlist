package com.wishlist.repository;

import com.wishlist.model.Friend;
import com.wishlist.model.FriendKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendKey> {

    boolean existsById(FriendKey id);

    List<Friend> findById_UserId(Long userId);
}
