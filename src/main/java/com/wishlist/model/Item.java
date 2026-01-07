package com.wishlist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wishlist.model.enums.Currency;
import com.wishlist.model.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "wishlist")
@ToString(exclude = "wishlist")
public class Item {

    @Id
    @SequenceGenerator(name = "item_generator", sequenceName = "items_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "item_generator")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "shop_link", nullable = false)
    private String shopLink;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency = Currency.USD;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;
}
