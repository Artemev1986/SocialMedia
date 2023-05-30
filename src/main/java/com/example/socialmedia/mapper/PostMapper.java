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

import java.time.LocalDateTime;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(expression = "java(UserMapper.INSTANCE.toUserShortDto(post.getUser()))", target = "user")
    @Mapping(source = "createdAt", target = "createdAt")
    ResponseNewPost toResponseNewPost(Post post);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(expression = "java(UserMapper.INSTANCE.toUserShortDto(post.getUser()))", target = "user")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ResponseUpdatePost toResponseUpdatedPost(Post post);

    @Mapping(source = "requestPost.title", target = "title")
    @Mapping(source = "requestPost.text", target = "text")
    @Mapping(source = "user", target = "user")
    @Mapping(expression = "java(now())", target = "createdAt")
    Post toPost(RequestPost requestPost, User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "text", target = "text")
    @Mapping(expression = "java(UserMapper.INSTANCE.toUserShortDto(post.getUser()))", target = "user")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(expression = "java(ImageMapper.INSTANCE.toImageDtos(post.getImages()))", target = "images")
    ResponsePost toResponsePost(Post post);

    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}