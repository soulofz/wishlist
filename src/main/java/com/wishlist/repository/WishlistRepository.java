package com.wishlist.repository;

import com.wishlist.model.User;
import com.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findAllByOwner(User owner);
}
