package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LibrarianInputDto {

    @NotNull
    @Size(min = 3, max = 250)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 250)
    private String lastName;
}
