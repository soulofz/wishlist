package com.wishlist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponseDto {

    private String name;
    private LocalDate endDate;
    private int count;
}
