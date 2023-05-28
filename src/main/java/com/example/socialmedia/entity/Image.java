package com.example.socialmedia.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "images")
public class Image extends BaseEntity {
    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
