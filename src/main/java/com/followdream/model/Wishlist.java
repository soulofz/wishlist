package com.followdream.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "wishlists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "items")
@ToString(exclude = "items")

public class Wishlist {

    @Id
    @SequenceGenerator(name = "wishlist_generator", sequenceName = "wishlist_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "wishlist_generator")
    private Long id;

    private String name;

    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL)
    private List<Item> items;
}
