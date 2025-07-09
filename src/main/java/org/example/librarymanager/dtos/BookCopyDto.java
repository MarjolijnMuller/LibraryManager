package org.example.librarymanager.dtos;

import org.example.librarymanager.models.BookCopyStatus;

public class BookCopyDto {
    public Long bookCopyId;
    public Long followNumber;
    public BookCopyStatus status;
    public Long bookId;
    public String bookTitle;
    public String bookAuthor;
}