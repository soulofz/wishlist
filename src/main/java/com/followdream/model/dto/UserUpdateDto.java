package com.followdream.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthday;

    @JsonIgnore
    private Integer age;

    private File avatar;

}
