package com.wishlist.model;

import com.wishlist.model.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "friend_requests")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
@Data
public class FriendRequest {

    @Id
    @SequenceGenerator(name = "friend_request_status_generator", sequenceName = "friend_requests_status_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "friend_request_status_generator")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status = FriendRequestStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();
}
