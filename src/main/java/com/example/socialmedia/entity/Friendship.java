package com.example.socialmedia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "friendships")
@Schema(description = "Friendship entity")
public class Friendship extends BaseEntity {
    @Column(name = "user_id")
    @Schema(description = "User ID")
    private Long userId;
    @Schema(description = "Friend ID")
    @Column(name = "friend_id")
    private Long friendId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Schema(description = "Friendship status")
    StatusFriendship status;
}