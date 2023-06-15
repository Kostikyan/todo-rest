package com.todorest.dto;

import com.todorest.entity.Type.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDto {
    private int id;
    private String title;
    private StatusType statusType;
    private CategoryDto category;
    private UserDto user;
}
