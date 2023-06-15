package com.todorest.mapper;

import com.todorest.dto.CategoryDto;
import com.todorest.dto.CreateCategoryRequestDto;
import com.todorest.dto.CreateUserRequestDto;
import com.todorest.dto.UserDto;
import com.todorest.entity.Category;
import com.todorest.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category map(CreateCategoryRequestDto dto);

    CategoryDto mapToDto(Category entity);

}