package com.wishlist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
}
