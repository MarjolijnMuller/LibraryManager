package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserInputDto {
    @Size(min = 5, max = 50)
    @NotNull
    public String username;


    @NotNull
    @Size(min = 5, max = 250)
    public String password;

    @NotNull
    public Set<String> roles;
}
