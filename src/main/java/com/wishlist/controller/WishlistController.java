package com.wishlist.controller;

import com.wishlist.model.Wishlist;
import com.wishlist.model.dto.ItemRequestDto;
import com.wishlist.model.dto.WishlistExtendedResponseDto;
import com.wishlist.model.dto.WishlistRequestDto;
import com.wishlist.model.dto.WishlistResponseDto;
import com.wishlist.service.ItemService;
import com.wishlist.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;
    private final ItemService itemService;

    public WishlistController(WishlistService wishlistService, ItemService itemService) {
        this.wishlistService = wishlistService;
        this.itemService = itemService;
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
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        if (wishlistFromDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);

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
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/{id:[0-9]+}/items")
    ResponseEntity<WishlistExtendedResponseDto> addItem(@PathVariable("id") Long id, @ModelAttribute ItemRequestDto itemRequestDto, @RequestPart(required = false) MultipartFile image) throws IOException {
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        if (wishlistFromDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        itemService.createItem(itemRequestDto, image, wishlistFromDB);
        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);

        return ResponseEntity.ok(wishlist);
    }

    @PutMapping(("/{id:[0-9]+}/items/{itemId:[0-9]+}"))
    ResponseEntity<WishlistExtendedResponseDto> updateItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId, @ModelAttribute ItemRequestDto itemRequestDto, @RequestPart(required = false) MultipartFile image) throws IOException {
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        if (wishlistFromDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        itemService.updateItem(itemId, itemRequestDto, image);
        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping(("/{id:[0-9]+}/items/{itemId:[0-9]+}"))
    ResponseEntity<WishlistExtendedResponseDto> deleteItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId) throws IOException {
    Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
    if (wishlistFromDB == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    itemService.deleteItem(itemId,wishlistFromDB);
    WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);
    return ResponseEntity.ok(wishlist);
    }

    @PutMapping(("/{id:[0-9]+}/items/{itemId:[0-9]+}/reserve"))
    ResponseEntity<WishlistExtendedResponseDto> reserveItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId) throws IOException {
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        if (wishlistFromDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        itemService.reserveItem(itemId);
        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);
        return ResponseEntity.ok(wishlist);
    }

    @PutMapping(("/{id:[0-9]+}/items/{itemId:[0-9]+}/unreserve"))
    ResponseEntity<WishlistExtendedResponseDto> unreserveItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId) throws IOException {
        Wishlist wishlistFromDB = wishlistService.getWishlistById(id);
        if (wishlistFromDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        itemService.unreserveItem(itemId);
        WishlistExtendedResponseDto wishlist = wishlistService.convertToExtendedDto(wishlistFromDB);
        return ResponseEntity.ok(wishlist);
    }
}
