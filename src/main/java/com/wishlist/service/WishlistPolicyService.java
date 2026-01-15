package com.wishlist.service;

import com.wishlist.model.User;
import com.wishlist.model.Wishlist;
import com.wishlist.model.enums.CompletedGiftPolicy;
import com.wishlist.model.enums.ReservationVisibilityPolicy;
import org.springframework.stereotype.Service;

@Service
public class WishlistPolicyService {

    private final FriendService friendService;

    public WishlistPolicyService(FriendService friendService) {
        this.friendService = friendService;
    }

    public boolean isVisibleForUser(Wishlist wishlist, User requester) {

        if (wishlist.getOwner().equals(requester)) {
            return true;
        }

        switch (wishlist.getVisibilityPolicy()) {
            case PUBLIC -> {
                return true;
            }
            case FRIENDS_ONLY -> {
                return friendService.areFriends(wishlist.getOwner(), requester);
            }
            case PRIVATE -> {
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean canReserveItems(Wishlist wishlist, User requester) {
        if (requester == null) {
            return false;
        }
        if (wishlist.getOwner().equals(requester)) {
            return false;
        }
        switch (wishlist.getReservationPolicy()) {
            case PUBLIC -> {
                return true;
            }
            case FRIENDS_ONLY -> {
                return friendService.areFriends(wishlist.getOwner(), requester);
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isOwnerSeeOnlyReservedStatus(Wishlist wishlist) {
        return wishlist.getReservationVisibilityPolicy()
                == ReservationVisibilityPolicy.ANON_VISIBLE;
    }

    public boolean isReservationVisibleToOwner(Wishlist wishlist) {
        return wishlist.getReservationVisibilityPolicy()
                != ReservationVisibilityPolicy.HIDDEN;
    }

    public boolean shouldRemoveCompletedGift(Wishlist wishlist) {
        return wishlist.getCompletedGiftPolicy()
                == CompletedGiftPolicy.REMOVE;
    }
}
