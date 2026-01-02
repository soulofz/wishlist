package com.wishlist.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "security")
@ToString(exclude = "security")
public class User {

    @Id
    @SequenceGenerator(name = "user_generator", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_generator")
    private Long id;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private int age;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthday;

    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "user", cascade = CascadeType.ALL)
    private Security security;


    @PrePersist
    private void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        calculateAge(); // Рассчитываем возраст при создании
    }

    @PreUpdate
    private void onUpdate() {
        this.updated = LocalDateTime.now();
        calculateAge();
    }

    @PostLoad
    private void onLoad() {
        calculateAge();
    }

    private void calculateAge() {
        if (this.birthday != null) {
            this.age = Period.between(this.birthday, LocalDate.now()).getYears();
        }
    }

}
