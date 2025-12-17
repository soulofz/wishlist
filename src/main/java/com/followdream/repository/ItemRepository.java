package com.followdream.repository;

import com.followdream.model.Item;
import com.followdream.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByWishlistId(Long wishlistId);
    List<Item> findByStatus(ItemStatus status);
    List<Item> findAllReservedById(Long userId);
}
