package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.BookDto;
import org.example.librarymanager.dtos.BookInputDto;
import org.example.librarymanager.models.Book;

import java.util.List;
import java.util.stream.Collectors;


public class BookMapper {
    public static Book toEntity(BookInputDto bookInputDto) {
        Book book = new Book(
                bookInputDto.title,
                bookInputDto.authorFirstName,
                bookInputDto.authorLastName,
                bookInputDto.ISBN,
                bookInputDto.publisher,
                bookInputDto.category);
        return book;
    }

    public static BookDto toResponseDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.bookId = book.getBookId();
        bookDto.title = book.getTitle();
        bookDto.authorFirstName = book.getAuthorFirstName();
        bookDto.authorLastName = book.getAuthorLastName();
        bookDto.ISBN = book.getISBN();
        bookDto.publisher = book.getPublisher();
        bookDto.category = book.getCategory();
        return bookDto;
    }

    public static List<BookDto> toResponseDtoList(List<Book> books) {
        return books.stream()
                .map(BookMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}