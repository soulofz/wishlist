package com.wishlist.model.dto;

import com.wishlist.model.enums.CompletedGiftPolicy;
import com.wishlist.model.enums.ReservationPolicy;
import com.wishlist.model.enums.ReservationVisibilityPolicy;
import com.wishlist.model.enums.VisibilityPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor

public class WishlistRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private VisibilityPolicy visibilityPolicy;

    @NotNull
    private ReservationPolicy reservationPolicy;

    @NotNull
    private ReservationVisibilityPolicy reservationVisibilityPolicy;

    @NotNull
    private CompletedGiftPolicy completedGiftPolicy;
}
