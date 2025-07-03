package org.example.librarymanager.dtos;

import org.example.librarymanager.models.BookCopyStatus;

public class BookCopyInputDto {
    public BookCopyStatus status;
    public String ISBN;
    public String title;
    public String authorFirstName;
    public String authorLastName;
    public String publisher;
    public String category;
}