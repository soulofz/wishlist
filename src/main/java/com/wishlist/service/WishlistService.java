package com.wishlist.service;

import com.wishlist.model.User;
import com.wishlist.model.Wishlist;
import com.wishlist.model.dto.WishlistExtendedResponseDto;
import com.wishlist.model.dto.WishlistRequestDto;
import com.wishlist.model.dto.WishlistResponseDto;
import com.wishlist.repository.WishlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final WishlistPolicyService wishlistPolicyService;

    public WishlistService(WishlistRepository wishlistRepository,
                           UserService userService,
                           WishlistPolicyService wishlistPolicyService) {
        this.wishlistRepository = wishlistRepository;
        this.userService = userService;
        this.wishlistPolicyService = wishlistPolicyService;
    }

    private static Wishlist mapToEntity(WishlistRequestDto wishlistRequestDto) {
        Wishlist wishlist = new Wishlist();
        wishlist.setName(wishlistRequestDto.getName().trim());
        wishlist.setEndDate(wishlistRequestDto.getEndDate());
        wishlist.setVisibilityPolicy(wishlistRequestDto.getVisibilityPolicy());
        wishlist.setReservationPolicy(wishlistRequestDto.getReservationPolicy());
        wishlist.setReservationVisibilityPolicy(wishlistRequestDto.getReservationVisibilityPolicy());
        wishlist.setCompletedGiftPolicy(wishlistRequestDto.getCompletedGiftPolicy());
        return wishlist;
    }

    private WishlistExtendedResponseDto convertToExtendedDto(Wishlist wishlist) {
        WishlistExtendedResponseDto responseDto = new WishlistExtendedResponseDto();
        responseDto.setName(wishlist.getName());
        responseDto.setEndDate(wishlist.getEndDate());
        responseDto.setCount(wishlist.getCount());
        // TODO добавить лист подарков
        return responseDto;
    }

    private  WishlistResponseDto convertToResponseDto(Wishlist wishlist) {
        WishlistResponseDto responseDto = new WishlistResponseDto();
        responseDto.setName(wishlist.getName());
        responseDto.setEndDate(wishlist.getEndDate());
        responseDto.setCount(wishlist.getCount());
        return responseDto;
    }

    public void validateWishlistRequest(WishlistRequestDto wishlistRequestDto) {
        List<String> errors = new ArrayList<>();

        if (wishlistRequestDto.getName() == null
                || wishlistRequestDto.getName().isBlank()) {
            errors.add("Invalid wishlist name");
        }
        if (wishlistRequestDto.getEndDate() == null) {
            errors.add("Invalid wishlist end date");
        }
        if (wishlistRequestDto.getEndDate().isBefore(LocalDate.now())) {
            errors.add("End date cannot be in the past");
        }
        if (wishlistRequestDto.getVisibilityPolicy() == null
                || wishlistRequestDto.getReservationPolicy() == null
                || wishlistRequestDto.getReservationVisibilityPolicy() == null
                || wishlistRequestDto.getCompletedGiftPolicy() == null) {
            errors.add("Wishlist policies must be specified");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    @Transactional
    public Wishlist createWishlist(WishlistRequestDto wishlistRequestDto) {
        User user = userService.getCurrentUser();

        validateWishlistRequest(wishlistRequestDto);

        Wishlist wishlist = mapToEntity(wishlistRequestDto);
        wishlist.setOwner(user);
        wishlist.setCreated(LocalDateTime.now());
        wishlist.setUpdated(LocalDateTime.now());

        log.info("Creating wishlist '{}' for user '{}'", wishlist.getName(), user.getSecurity().getUsername());

        return wishlistRepository.save(wishlist);

    }

    @Transactional
    public Wishlist updateWishlist(Long id, WishlistRequestDto wishlistRequestDto) {
        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (!wishlist.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Only owner can update wishlist");
        }

        validateWishlistRequest(wishlistRequestDto);

        wishlist.setName(wishlistRequestDto.getName().trim());
        wishlist.setEndDate(wishlistRequestDto.getEndDate());
        wishlist.setVisibilityPolicy(wishlistRequestDto.getVisibilityPolicy());
        wishlist.setReservationPolicy(wishlistRequestDto.getReservationPolicy());
        wishlist.setReservationVisibilityPolicy(wishlistRequestDto.getReservationVisibilityPolicy());
        wishlist.setCompletedGiftPolicy(wishlistRequestDto.getCompletedGiftPolicy());
        wishlist.setUpdated(LocalDateTime.now());

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void deleteWishlist(Long id) {
        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found with id: " + id));

        if (!wishlist.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Only owner can delete wishlist");
        }

        log.info("Deleting wishlist '{}' (id: {}) for user '{}'",
                wishlist.getName(), wishlist.getId(), user.getSecurity().getUsername());

        wishlistRepository.delete(wishlist);
    }

    public List<WishlistResponseDto> getMyWishlists() {
        User user = userService.getCurrentUser();

        List<Wishlist> wishlists = wishlistRepository.findAllByOwner(user);
        List<WishlistResponseDto> result = new ArrayList<>();

        for (Wishlist wishlist : wishlists) {
            result.add(convertToResponseDto(wishlist));
        }

        result.sort(buildWishlistComparator());
        return result;
    }

    private void checkWishlistAccess(Wishlist wishlist, User viewer) {
        if (viewer != null && wishlist.getOwner().getId().equals(viewer.getId())) {
            return;
        }

        if (!wishlistPolicyService.isVisibleForUser(wishlist, viewer)) {
            log.warn("Access denied to wishlist {} for user {}",
                    wishlist.getId(), viewer != null ? viewer.getId() : "anonymous");
            throw new SecurityException("You are not allowed to view this wishlist");
        }
    }

    public WishlistExtendedResponseDto getWishlistById(long id) {

        User viewer = tryToGetCurrentUser();

        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid wishlist id"));

        checkWishlistAccess(wishlist, viewer);

        return convertToExtendedDto(wishlist);
    }

    public List<WishlistResponseDto> getAllUserWishlists(String username) {

        User viewer = tryToGetCurrentUser();
        User target = userService.getUserByUsername(username);

        List<Wishlist> wishlists = wishlistRepository.findAllByOwner(target);
        List<WishlistResponseDto> result = new ArrayList<>();

        for (Wishlist wishlist : wishlists) {

            if ((viewer != null && viewer.getId().equals(target.getId())) ||
                    wishlistPolicyService.isVisibleForUser(wishlist, viewer)) {
                result.add(convertToResponseDto(wishlist));
            }
        }

        result.sort(buildWishlistComparator());
        return result;
    }

    private User tryToGetCurrentUser() {
        try {
            return userService.getCurrentUser();
        } catch (Exception e) {
            log.debug("No authenticated user found", e);
            return null;
        }
    }

    private Comparator<WishlistResponseDto> buildWishlistComparator() {
        return Comparator
                .comparing(WishlistResponseDto::getEndDate)
                .thenComparing(WishlistResponseDto::getName, String.CASE_INSENSITIVE_ORDER);
    }
}
