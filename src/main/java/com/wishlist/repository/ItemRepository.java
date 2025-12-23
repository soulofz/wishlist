package com.wishlist.repository;

import com.wishlist.model.Item;
import com.wishlist.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByWishlistId(Long wishlistId);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findAllReservedById(Long userId);
}
