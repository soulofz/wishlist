package com.followdream.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "users")
@Data
@EqualsAndHashCode(exclude = "security")
@ToString(exclude = "security")
@Component
public class User {

    @Id
    @SequenceGenerator(name = "user_generator", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_generator")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private int age;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Date birthday;

    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "user", cascade = CascadeType.ALL)
    private Security security;
}
