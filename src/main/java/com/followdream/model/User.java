package com.followdream.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"security", "wishlists"})
@ToString(exclude = {"security", "wishlists"})
public class User {

    @Id
    @SequenceGenerator(name = "user_generator", sequenceName = "user_id_seq", allocationSize = 1)
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

    private LocalDate birthday;

    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "user", cascade = CascadeType.ALL)
    private Security security;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Wishlist> wishlists;
}
