package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.BookDto;
import org.example.librarymanager.dtos.BookInputDto;
import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;

import java.util.List;
import java.util.stream.Collectors;


public class BookMapper {
    public static Book toEntity(BookInputDto bookInputDto) {
        if (bookInputDto == null){
            return null;
        }
        Book book = new Book();
        book.setTitle(bookInputDto.title);
        book.setAuthorFirstName(bookInputDto.authorFirstName);
        book.setAuthorLastName(bookInputDto.authorLastName);
        book.setISBN(bookInputDto.ISBN);
        book.setPublisher(bookInputDto.publisher);
        if (bookInputDto.category != null && !bookInputDto.category.trim().isEmpty()) {
            try {
                book.setCategory(BookCategory.valueOf(bookInputDto.category.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid book category provided: '" + bookInputDto.category + "'. Setting category to null.");
                book.setCategory(null);
            }
        } else {

            book.setCategory(null);
        }
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
        bookDto.totalCopies = book.getCopies() != null ? book.getCopies().size() : 0;
        return bookDto;
    }

    public static List<BookDto> toResponseDtoList(List<Book> books) {
        return books.stream()
                .map(BookMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}