package com.example.socialmedia.mapper;

import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.dto.UserShortDto;
import com.example.socialmedia.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "name")
    UserDto toUserDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    UserShortDto toUserShortDto(User user);

    User registrationToUser(RegistrationRequest registrationRequest);
}