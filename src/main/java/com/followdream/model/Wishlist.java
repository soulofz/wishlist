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
@EqualsAndHashCode(exclude = {"user", "items"})
@ToString(exclude = {"user", "items"})

public class Wishlist {

    @Id
    @SequenceGenerator(name = "wishlist_generator", sequenceName = "wishlist_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "wishlist_generator")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL)
    private List<Item> items;
}
