package com.wishlist.model;

import com.wishlist.model.enums.CompletedGiftPolicy;
import com.wishlist.model.enums.ReservationPolicy;
import com.wishlist.model.enums.ReservationVisibilityPolicy;
import com.wishlist.model.enums.VisibilityPolicy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @SequenceGenerator(name = "wishlist_generator", sequenceName = "wishlists_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "wishlist_generator")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL)
    private List<Item> items;

    @Column(nullable = false)
    private int count = 0;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private VisibilityPolicy visibilityPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation", nullable = false)
    private ReservationPolicy reservationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_visibility", nullable = false)
    private ReservationVisibilityPolicy reservationVisibilityPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "completed_gifts", nullable = false)
    private CompletedGiftPolicy completedGiftPolicy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
