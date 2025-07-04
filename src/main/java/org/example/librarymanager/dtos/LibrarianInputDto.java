package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LibrarianInputDto extends UserInputDto {

    @NotNull
    @Size(min = 3, max = 250)
    public String firstName;

    @NotNull
    @Size(min = 3, max = 250)
    public String lastName;
}
