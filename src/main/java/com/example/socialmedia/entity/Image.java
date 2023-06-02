package com.example.socialmedia.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.type.BinaryType;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "images")
@TypeDefs(@TypeDef(name = "binary", typeClass = BinaryType.class))
public class Image extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "content_type", nullable = false)
    private String contentType;
    @Lob
    @Column(name = "image_data", nullable = false)
    @Type(type = "binary")
    @ToString.Exclude
    private byte[] imageData;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
