package com.wishlist.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthday;

    @JsonIgnore
    private Integer age;

}
