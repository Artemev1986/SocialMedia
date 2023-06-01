package com.example.socialmedia.mapper;

import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.entity.Message;
import com.example.socialmedia.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {

    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(expression = "java(null)", target = "id")
    @Mapping(source = "newMessage.text", target = "text")
    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "recipient", target = "recipient")
    @Mapping(expression = "java(now())", target = "createdAt")
    Message toMessage(NewMessage newMessage, User sender, User recipient);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "text", target = "text")
    @Mapping(expression = "java(UserMapper.INSTANCE.toUserShortDto(message.getSender()))", target = "sender")
    @Mapping(expression = "java(UserMapper.INSTANCE.toUserShortDto(message.getRecipient()))", target = "recipient")
    @Mapping(source = "createdAt", target = "createdAt")
    ResponseMessage toResponseMessage(Message message);

    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}