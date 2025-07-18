package org.example.librarymanager.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    public Long userId;
    public String username;
    public Set<String> roles;
}
