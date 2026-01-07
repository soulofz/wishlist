package com.wishlist.controller;

import com.wishlist.model.Wishlist;
import com.wishlist.model.dto.WishlistExtendedResponseDto;
import com.wishlist.model.dto.WishlistRequestDto;
import com.wishlist.model.dto.WishlistResponseDto;
import com.wishlist.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    ResponseEntity<List<WishlistResponseDto>> getMyWishlists() {
        List<WishlistResponseDto> wishlists = wishlistService.getMyWishlists();
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/user/{username}")
    ResponseEntity<List<WishlistResponseDto>> getWishlistsForUser(@PathVariable String username) {
        List<WishlistResponseDto> wishlists = wishlistService.getAllUserWishlists(username);
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/{id:[0-9]+}")
    ResponseEntity<WishlistExtendedResponseDto> getWishlistById(@PathVariable Long id) {
        WishlistExtendedResponseDto wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping
    ResponseEntity<WishlistExtendedResponseDto> createWishlist(@RequestBody WishlistRequestDto wishlistRequestDto) {
        WishlistExtendedResponseDto wishlist = wishlistService.createWishlist(wishlistRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlist);
    }

    @DeleteMapping("/{id:[0-9]+}")
    ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id:[0-9]+}")
    ResponseEntity<WishlistExtendedResponseDto> updateWishlist(@PathVariable("id") Long id, @RequestBody WishlistRequestDto wishlistRequestDto) {
        wishlistService.updateWishlist(id, wishlistRequestDto);
        WishlistExtendedResponseDto wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlist);
    }
}
