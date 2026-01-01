package com.wishlist.service;

import com.wishlist.exception.UserNotFoundException;
import com.wishlist.model.Friend;
import com.wishlist.model.FriendKey;
import com.wishlist.model.User;
import com.wishlist.model.dto.FriendResponseDto;
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
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final SecurityService securityService;

    public FriendService(FriendRequestRepository friendRequestRepository,
                         FriendRepository friendRepository,
                         UserService userService,
                         SecurityService securityService) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendRepository = friendRepository;
        this.userService = userService;
        this.securityService = securityService;
    }


    @Transactional
    public void removeFriend(String username) {
        User currentUser = userService.getCurrentUser();
        User friend = userService.getUserByUsername(username);

        friendRepository.deleteById(new FriendKey(currentUser.getId(), friend.getId()));
        friendRepository.deleteById(new FriendKey(friend.getId(), currentUser.getId()));
    }


    public List<FriendResponseDto> getFriends() {
        User currentUser = userService.getCurrentUser();

        List<Friend> friends = friendRepository.findById_UserId(currentUser.getId());
        List<FriendResponseDto> friendsResponseDto = new ArrayList<>();
        for (Friend friend : friends) {
            Long userId = friend.getId().getFriendId();
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            FriendResponseDto friendResponseDto = new FriendResponseDto();
            friendResponseDto.setUsername(user.getSecurity().getUsername());
            friendResponseDto.setFirstName(user.getFirstName());
            friendResponseDto.setLastName(user.getLastName());
            if (user.getAvatarPath() != null) {
                friendResponseDto.setAvatarUrl("/" + user.getAvatarPath());
            }
            friendsResponseDto.add(friendResponseDto);
        }
        return friendsResponseDto;
    }
}