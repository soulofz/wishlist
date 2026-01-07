package com.wishlist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistExtendedResponseDto {

    private String name;
    private LocalDate endDate;
    private int count;
    private List<ItemResponseDto> items;
}
