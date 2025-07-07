package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserInputDto {
    @NotNull
    @Size(min = 5, max = 50)
    public String username;

    @Email
    @Size(min = 3, max = 250)
    public String email;

    @NotNull
    @Size(min = 5, max = 50)
    public String password;

    public String profilePictureUrl;
}
