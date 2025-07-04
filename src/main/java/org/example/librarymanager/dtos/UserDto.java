package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {
    public Long userId;
    public String username;
    public String email;
    public String password;
    public String profilePictureUrl;
}
