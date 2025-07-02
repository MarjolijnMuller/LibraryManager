package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.librarymanager.models.BookCategory;

public class BookInputDto {
    @NotNull
    public String title;
    @NotNull
    public String authorFirstName;
    @NotNull
    public String authorLastName;
    @NotNull
    @Size(min = 10, max = 13)
    public String ISBN;
    public String publisher;
    @NotNull
    public BookCategory category;
}
