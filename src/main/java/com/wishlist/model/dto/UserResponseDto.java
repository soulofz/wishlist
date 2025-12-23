package com.wishlist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private Integer age;
    private LocalDate birthday;
    private String avatarUrl;
}
