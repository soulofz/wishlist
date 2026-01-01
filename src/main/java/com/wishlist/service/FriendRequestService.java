package com.wishlist.service;

import com.wishlist.exception.FriendRequestNotFoundException;
import com.wishlist.model.Friend;
import com.wishlist.model.FriendKey;
import com.wishlist.model.FriendRequest;
import com.wishlist.model.User;
import com.wishlist.model.dto.FriendRequestDto;
import com.wishlist.model.enums.FriendRequestStatus;
import com.wishlist.repository.FriendRepository;
import com.wishlist.repository.FriendRequestRepository;
import com.wishlist.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FriendRequestService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final SecurityService securityService;

    public FriendRequestService(FriendRequestRepository friendRequestRepository,
                                FriendRepository friendRepository,
                                UserService userService,
                                SecurityService securityService) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendRepository = friendRepository;
        this.userService = userService;
        this.securityService = securityService;
    }

    private void createFriendRequest(User sender, User receiver, FriendRequestStatus status) {
        FriendRequest newRequest = new FriendRequest();
        newRequest.setSender(sender);
        newRequest.setReceiver(receiver);
        newRequest.setStatus(status);
        newRequest.setCreated(LocalDateTime.now());
        friendRequestRepository.save(newRequest);
    }

    @Transactional
    public void sendFriendRequest(String receiverUsername) throws FriendRequestNotFoundException {
        User sender = userService.getCurrentUser();
        User receiver = userService.getUserByUsername(receiverUsername);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself");
        }

        Optional<FriendRequest> existingRequest = friendRequestRepository
                .findTopBySenderIdAndReceiverIdOrderByCreatedDesc(sender.getId(), receiver.getId());

        if (existingRequest.isPresent()) {
            FriendRequest lastRequest = existingRequest.get();

            if (lastRequest.getStatus() == FriendRequestStatus.REJECTED || lastRequest.getStatus() == FriendRequestStatus.CANCELLED) {
                createFriendRequest(sender, receiver, FriendRequestStatus.PENDING);
                return;
            }

            if (lastRequest.getStatus() == FriendRequestStatus.PENDING) {
                throw new IllegalStateException("Friend request is already in progress");
            }

            if (lastRequest.getStatus() == FriendRequestStatus.ACCEPTED) {
                FriendKey key = new FriendKey(sender.getId(), receiver.getId());
                if (!friendRepository.existsById(key)) {
                    createFriendRequest(sender, receiver, FriendRequestStatus.PENDING);
                } else {
                    throw new IllegalStateException("You are already friends");
                }
            }
        } else {
            createFriendRequest(sender, receiver, FriendRequestStatus.PENDING);
        }
    }


    public void acceptFriendRequest(String senderUsername) throws FriendRequestNotFoundException {
        User receiver = userService.getCurrentUser();
        User sender = userService.getUserByUsername(senderUsername);

        FriendRequest request = friendRequestRepository
                .findTopBySenderIdAndReceiverIdAndStatusOrderByCreatedDesc(sender.getId(), receiver.getId(), FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(sender.getId(), receiver.getId()));

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        friendRepository.save(new Friend(new FriendKey(sender.getId(), receiver.getId())));
        friendRepository.save(new Friend(new FriendKey(receiver.getId(), sender.getId())));
    }

    @Transactional
    public void rejectFriendRequest(String senderUsername) throws FriendRequestNotFoundException {
        User receiver = userService.getCurrentUser();
        User sender = userService.getUserByUsername(senderUsername);

        FriendRequest request = friendRequestRepository
                .findTopBySenderIdAndReceiverIdAndStatusOrderByCreatedDesc(sender.getId(), receiver.getId(), FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(sender.getId(), receiver.getId()));

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    @Transactional
    public void cancelFriendRequest(String receiverUsername) throws FriendRequestNotFoundException {
        User sender = userService.getCurrentUser();
        User receiver = userService.getUserByUsername(receiverUsername);

        FriendRequest request = friendRequestRepository
                .findTopBySenderIdAndReceiverIdAndStatusOrderByCreatedDesc(sender.getId(), receiver.getId(), FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(sender.getId(), receiver.getId()));

        request.setStatus(FriendRequestStatus.CANCELLED);
        friendRequestRepository.save(request);
    }

    private FriendRequestDto createFriendRequestDto(FriendRequest request, User relatedUser) {
        User user = (relatedUser.equals(request.getSender())) ? request.getReceiver() : request.getSender();
        FriendRequestDto dto = new FriendRequestDto();
        dto.setUsername(user.getSecurity().getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setStatus(request.getStatus());
        dto.setCreated(request.getCreated());
        if (user.getAvatarPath() != null) {
            dto.setAvatarUrl("/" + user.getAvatarPath());
        }
        return dto;
    }

    public List<FriendRequestDto> getIncomingRequests(Long currentUserId) {
        User currentUser = userService.getCurrentUser();

        List<FriendRequest> requests = friendRequestRepository.findAllByReceiverIdAndStatus(currentUser.getId(), FriendRequestStatus.PENDING);
        List<FriendRequestDto> responseList = new ArrayList<>();

        for (FriendRequest request : requests) {
            FriendRequestDto dto = createFriendRequestDto(request, currentUser);
            responseList.add(dto);
        }
        return responseList;
    }

    public List<FriendRequestDto> getOutgoingRequests(Long currentUserId) {
        User currentUser = userService.getCurrentUser();

        List<FriendRequest> requests = friendRequestRepository.findAllBySenderIdAndStatus(currentUser.getId(), FriendRequestStatus.PENDING);
        List<FriendRequestDto> responseList = new ArrayList<>();

        for (FriendRequest request : requests) {
            FriendRequestDto dto = createFriendRequestDto(request, currentUser);
            responseList.add(dto);
        }
        return responseList;
    }
}
