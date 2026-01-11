package com.wishlist.model.dto;

import com.wishlist.model.enums.Currency;
import com.wishlist.model.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {

    private String name;
    private String description;
    private String shopLink;

    private Long price;
    private Currency currency;

    private String imageUrl;

    private ItemStatus status;
    private String reservedBy;

}
