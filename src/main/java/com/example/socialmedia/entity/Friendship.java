package com.example.socialmedia.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "friendships")
public class Friendship extends BaseEntity {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "friend_id")
    private Long friendId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StatusFriendship status;
}