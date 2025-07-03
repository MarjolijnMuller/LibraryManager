package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.BookCopyDto;
import org.example.librarymanager.models.BookCopy;

import java.util.List;
import java.util.stream.Collectors;

public class BookCopyMapper {

    public static BookCopyDto toResponseDto(BookCopy bookCopy) {
        if (bookCopy == null) {
            return null;
        }

        BookCopyDto dto = new BookCopyDto();
        dto.bookCopiesId = bookCopy.getBookCopiesId();
        dto.followNumber = bookCopy.getFollowNumber();
        dto.status = bookCopy.getStatus();

        if (bookCopy.getBook() != null) {
            dto.bookId = bookCopy.getBook().getBookId();
            dto.bookTitle = bookCopy.getBook().getTitle();
            dto.bookAuthor = bookCopy.getBook().getAuthorFirstName() + " " + bookCopy.getBook().getAuthorLastName();
        } else {
            dto.bookId = null;
            dto.bookTitle = null;
            dto.bookAuthor = null;
        }
        return dto;
    }

    public static List<BookCopyDto> toDtoList(List<BookCopy> bookCopies) {
        if (bookCopies == null) {
            return java.util.Collections.emptyList();
        }
        return bookCopies.stream()
                .map(BookCopyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

}