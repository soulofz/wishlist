package com.followdream.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.followdream.model.enums.ItemStatus;
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
    @SequenceGenerator(name = "item_generator", sequenceName = "item_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "item_generator")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "shop_link", nullable = false)
    private String shopLink;

    @Column(nullable = false)
    private Long price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "reserved_by")
    private User reservedBy;
}
