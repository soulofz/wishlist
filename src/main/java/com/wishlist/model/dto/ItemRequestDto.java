package com.wishlist.model.dto;

import com.wishlist.model.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long price;

    @NotBlank
    private Currency currency;

    @NotBlank
    private String shopLink;

    private String imageLink;
}
