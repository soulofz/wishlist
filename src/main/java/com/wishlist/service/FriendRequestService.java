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

import java.util.ArrayList;
import java.util.List;

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


    @Transactional
    public void sendFriendRequest(String receiverUsername) throws FriendRequestNotFoundException {
        User sender = userService.getCurrentUser();
        User receiver = userService.getUserByUsername(receiverUsername);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You can not send friend request to yourself");
        }

        if (friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()).isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        FriendKey key = new FriendKey(sender.getId(), receiver.getId());
        if (friendRepository.existsById(key)) {
            throw new IllegalStateException("You are already friends");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);
    }

    public void acceptFriendRequest(String senderUsername) throws FriendRequestNotFoundException {
        User receiver = userService.getCurrentUser();
        User sender = userService.getUserByUsername(senderUsername);


        FriendRequest request = null;
        try {
            request = friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                    .orElseThrow(() -> new FriendRequestNotFoundException(sender.getId(), receiver.getId()));
        } catch (FriendRequestNotFoundException e) {
            log.error("Friend request not found{}", e.getMessage());
            throw new RuntimeException(e);
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

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
                .findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(() -> new FriendRequestNotFoundException(sender.getId(), receiver.getId()));

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    @Transactional
    public void cancelFriendRequest(String receiverUsername) throws FriendRequestNotFoundException {
        User sender = userService.getCurrentUser();
        User receiver = userService.getUserByUsername(receiverUsername);

        FriendRequest request = friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(() -> new FriendRequestNotFoundException(receiver.getId(), sender.getId()));

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("You cannot cancel an already processed request");
        }

        request.setStatus(FriendRequestStatus.CANCELLED);
        friendRequestRepository.save(request);
    }

    public List<FriendRequestDto> getIncomingRequests(Long currentUserId) {
        User currentUser = userService.getCurrentUser();

        List<FriendRequest> requests = friendRequestRepository.findAllByReceiverIdAndStatus(currentUser.getId(), FriendRequestStatus.PENDING);
        List<FriendRequestDto> responseList = new ArrayList<>();

        for (FriendRequest request : requests) {
            User sender = request.getSender();
            FriendRequestDto dto = new FriendRequestDto();
            dto.setUsername(sender.getSecurity().getUsername());
            dto.setFirstName(sender.getFirstName());
            dto.setLastName(sender.getLastName());
            dto.setStatus(request.getStatus());
            dto.setCreated(request.getCreated());
            if (sender.getAvatarPath() != null) {
                dto.setAvatarUrl("/" + sender.getAvatarPath());
            }
            responseList.add(dto);
        }
        return responseList;
    }

    public List<FriendRequestDto> getOutgoingRequests(Long currentUserId) {
        User currentUser = userService.getCurrentUser();

        List<FriendRequest> requests = friendRequestRepository.findAllBySenderIdAndStatus(currentUser.getId(), FriendRequestStatus.PENDING);
        List<FriendRequestDto> responseList = new ArrayList<>();

        for (FriendRequest request : requests) {
            User receiver = request.getReceiver();
            FriendRequestDto dto = new FriendRequestDto();
            dto.setUsername(receiver.getSecurity().getUsername());
            dto.setFirstName(receiver.getFirstName());
            dto.setLastName(receiver.getLastName());
            dto.setStatus(request.getStatus());
            dto.setCreated(request.getCreated());
            if (receiver.getAvatarPath() != null) {
                dto.setAvatarUrl("/" + receiver.getAvatarPath());
            }
            responseList.add(dto);
        }
        return responseList;
    }
}
