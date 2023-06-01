package com.example.socialmedia.mapper;

import com.example.socialmedia.dto.ImageDto;
import com.example.socialmedia.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImageMapper {

    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    ImageDto toImageDto(Image image);
}