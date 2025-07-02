package org.example.librarymanager.dtos;

import org.example.librarymanager.models.BookCategory;

public class BookDto {
    public Long bookId;
    public String title;
    public String authorFirstName;
    public String authorLastName;
    public String ISBN;
    public String publisher;
    public BookCategory category;

    //TODO: eventueel numberOfCopies?
}
