package com.todorest.mapper;

import com.todorest.dto.CreateTodoRequestDto;
import com.todorest.dto.TodoDto;
import com.todorest.entity.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    Todo map(CreateTodoRequestDto dto);

    TodoDto mapToDto(Todo entity);

}