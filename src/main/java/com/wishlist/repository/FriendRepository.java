package com.wishlist.repository;

import com.wishlist.model.Friend;
import com.wishlist.model.FriendKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, FriendKey> {

    boolean existsById(FriendKey id);

    List<Friend> findById_UserId(Long userId);

    Optional<Friend> findById_UserIdAndId_FriendId(Long userId, Long friendId);

    void deleteByIdUserIdAndIdFriendId(Long userId, Long friendId);
}
