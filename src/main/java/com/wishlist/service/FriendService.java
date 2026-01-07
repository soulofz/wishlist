package com.wishlist.service;

import com.wishlist.exception.UserNotFoundException;
import com.wishlist.model.Friend;
import com.wishlist.model.FriendKey;
import com.wishlist.model.User;
import com.wishlist.model.dto.FriendResponseDto;
import com.wishlist.repository.FriendRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserService userService;

    public FriendService(FriendRepository friendRepository,
                         UserService userService) {
        this.friendRepository = friendRepository;
        this.userService = userService;
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

    @Transactional(readOnly = true)
    public boolean areFriends(User user1, User user2) {
        if (user1 == null || user2 == null) {
            return false;
        }

        if (user1.getId().equals(user2.getId())) {
            return true;
        }

        return friendRepository.existsById(new FriendKey(user1.getId(), user2.getId()));
    }
}