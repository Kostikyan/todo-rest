package com.todorest.dto;

import com.todorest.entity.Type.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int id;
    private String name;
    private String surname;
    private String email;
    private UserType type;
}
