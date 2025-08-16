package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookInputDto {
    @NotNull
    @Size(min = 1, max = 250)
    public String title;
    @NotNull
    @Size(min = 3, max = 250)
    public String authorFirstName;
    @NotNull
    @Size(min = 3, max = 250)
    public String authorLastName;
    @NotNull
    @Size(min = 10, max = 13)
    public String ISBN;
    @Size(min = 3, max = 250)
    public String publisher;
    @NotNull
    public String category;
}
