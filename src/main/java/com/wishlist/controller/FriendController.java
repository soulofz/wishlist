package com.wishlist.controller;

import com.wishlist.exception.FriendRequestNotFoundException;
import com.wishlist.model.User;
import com.wishlist.model.dto.FriendRequestDto;
import com.wishlist.model.dto.FriendResponseDto;
import com.wishlist.service.FriendRequestService;
import com.wishlist.service.FriendService;
import com.wishlist.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;
    private final FriendRequestService friendRequestService;
    private final UserService userService;

    public FriendController(FriendService friendService, FriendRequestService friendRequestService, UserService userService) {
        this.friendService = friendService;
        this.friendRequestService = friendRequestService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteFriend(@PathVariable String username) {
        friendService.removeFriend(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/requests/send/{receiverUsername}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable String receiverUsername) throws FriendRequestNotFoundException {
        friendRequestService.sendFriendRequest(receiverUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/requests/accept/{senderUsername}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable String senderUsername) throws FriendRequestNotFoundException {
        friendRequestService.acceptFriendRequest(senderUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/requests/reject/{senderUsername}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable String senderUsername) throws FriendRequestNotFoundException {
        friendRequestService.rejectFriendRequest(senderUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/requests/cancel/{receiverUsername}")
    public ResponseEntity<Void> cancelFriendRequest(@PathVariable String receiverUsername) throws FriendRequestNotFoundException {
        friendRequestService.cancelFriendRequest(receiverUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestDto>> getIncomingFriends() {
        User currentUser = userService.getCurrentUser();
        List<FriendRequestDto> incomingRequests = friendRequestService.getIncomingRequests(currentUser.getId());
        return ResponseEntity.ok(incomingRequests);
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendRequestDto>> getOutgoingFriends() {
        User currentUser = userService.getCurrentUser();
        List<FriendRequestDto> outgoingRequests = friendRequestService.getOutgoingRequests(currentUser.getId());
        return ResponseEntity.ok(outgoingRequests);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MODERATOR')")
    @GetMapping("/requests/{username}")
    public ResponseEntity<Map<String, List<FriendRequestDto>>> getAllFriendRequestsForUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);

        List<FriendRequestDto> incomingRequests = friendRequestService.getIncomingRequests(user.getId());
        List<FriendRequestDto> outgoingRequests = friendRequestService.getOutgoingRequests(user.getId());

        Map<String, List<FriendRequestDto>> response = new HashMap<>();
        response.put("incoming", incomingRequests);
        response.put("outgoing", outgoingRequests);

        return ResponseEntity.ok(response);
    }
}
