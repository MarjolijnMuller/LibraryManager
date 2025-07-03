package org.example.librarymanager.dtos;

import org.example.librarymanager.models.BookCopyStatus;

public class BookCopyDto {
    public Long bookCopiesId;
    public Long followNumber;
    public BookCopyStatus status;
    public Long bookId;
    public String bookTitle;
    public String bookAuthor;
}