package com.wishlist.repository;

import com.wishlist.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByWishlistId(Long wishlistId);

    List<Item> findAllReservedByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM items ORDER BY RANDOM() LIMIT 50")
    List<Item> findRandomItems();
}
