package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.UserDto;
import org.example.librarymanager.models.User;
import org.example.librarymanager.models.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());

        Set<String> rolenames = user.getRoles().stream()
                .map(Role::getRolename)
                .collect(Collectors.toSet());
        dto.setRoles(rolenames);

        return dto;
    }
}
