package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.librarymanager.models.BookCopyStatus;

public class BookCopyInputDto {
    @NotNull
    public BookCopyStatus status = BookCopyStatus.AVAILABLE;

    @NotNull
    @Size(min = 10, max = 13)
    public String ISBN;

    @NotNull
    @Size(min = 1, max = 250)
    public String title;

    @NotNull
    @Size(min = 3, max = 250)
    public String authorFirstName;

    @NotNull
    @Size(min = 3, max = 250)
    public String authorLastName;

    @Size(min = 3, max = 250)
    public String publisher;

    @NotNull
    public String category;
}