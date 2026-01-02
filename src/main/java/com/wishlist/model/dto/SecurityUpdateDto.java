package com.wishlist.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SecurityUpdateDto {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Size(min = 8, message = "New password must be at least 8 characters long")
    @Pattern(regexp = ".*\\d.*", message = "New password must contain at least one digit")
    private String newPassword;

    @Email
    private String email;
}
