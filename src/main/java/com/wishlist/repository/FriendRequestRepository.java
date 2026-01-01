package com.wishlist.repository;

import com.wishlist.model.FriendRequest;
import com.wishlist.model.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<FriendRequest> findAllByReceiverIdAndStatus(Long receiverId, FriendRequestStatus status);

    List<FriendRequest> findAllBySenderIdAndStatus(Long senderId, FriendRequestStatus status);

    Optional<FriendRequest> findTopBySenderIdAndReceiverIdAndStatusOrderByCreatedDesc(Long senderId, Long receiverId, FriendRequestStatus status);

    Optional<FriendRequest> findTopBySenderIdAndReceiverIdOrderByCreatedDesc(Long senderId, Long receiverId);
}
