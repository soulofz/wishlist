package com.wishlist.model.dto;

import com.wishlist.model.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {

    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private FriendRequestStatus status;
    private LocalDateTime created;
}
