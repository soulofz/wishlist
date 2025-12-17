package com.followdream.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Item {

    @Id
    @SequenceGenerator(name = "item_generator", sequenceName = "item_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "item_generator")
    private Long id;

    private String name;
    private String link;
    private Long price;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;
}
