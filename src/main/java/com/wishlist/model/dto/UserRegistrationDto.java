package com.wishlist.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Data
@Component
public class UserRegistrationDto {

    @NotBlank
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    private String password;

    @Email
    private String email;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate birthday;

    @JsonIgnore
    private Integer age;

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        if (this.birthday != null) {
            this.age = Period.between(this.birthday, LocalDate.now()).getYears();
        }
    }

    public Integer getAge() {
        if (this.birthday != null) {
            return Period.between(this.birthday, LocalDate.now()).getYears();
        }
        return null;
    }
}
