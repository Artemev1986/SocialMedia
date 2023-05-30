package com.example.socialmedia.mapper;

import com.example.socialmedia.dto.RequestPost;
import com.example.socialmedia.dto.ResponseNewPost;
import com.example.socialmedia.dto.ResponsePost;
import com.example.socialmedia.dto.ResponseUpdatePost;
import com.example.socialmedia.entity.Post;
import com.example.socialmedia.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "createdAt", target = "createdAt")
    ResponseNewPost toResponseNewPost(Post post);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ResponseUpdatePost toResponseUpdatedPost(Post post);

    @Mapping(source = "requestPost.title", target = "title")
    @Mapping(source = "requestPost.text", target = "text")
    @Mapping(source = "user", target = "user")
    Post toPost(RequestPost requestPost, User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(expression = "java(ImageMapper.INSTANCE.toImageDtos(post.getImages()))", target = "images")
    ResponsePost toResponsePost(Post post);
}