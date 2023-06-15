package com.todorest.mapper;

import com.todorest.dto.CreateUserRequestDto;
import com.todorest.dto.UserDto;
import com.todorest.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User map(CreateUserRequestDto dto);

    UserDto mapToDto(User entity);

}