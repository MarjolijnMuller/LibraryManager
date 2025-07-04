package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserInputDto {
    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
    @Size(min = 5, max = 50)
    private String password;

    private String profilePictureUrl;
}
