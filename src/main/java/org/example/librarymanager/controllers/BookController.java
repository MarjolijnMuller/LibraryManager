package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.BookDto;
import org.example.librarymanager.dtos.BookInputDto;
import org.example.librarymanager.mappers.BookMapper;
import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;
import org.example.librarymanager.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<Book> allBooks = bookService.getAllBooks();
        List<BookDto> allBooksDto = BookMapper.toResponseDtoList(allBooks);
        return ResponseEntity.ok(allBooksDto);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookById(@PathVariable long bookId) {
        return ResponseEntity.ok(BookMapper.toResponseDto(bookService.getBookById(bookId)));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookDto>> getBookByTitle(@PathVariable String title) {
        List<Book> allBooksByTitle = bookService.getBooksByTitle(title);
        if (allBooksByTitle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<BookDto> allBooksByTitleDto = BookMapper.toResponseDtoList(allBooksByTitle);
        return ResponseEntity.ok(allBooksByTitleDto);
    }

    @GetMapping("/author/{authorLastName}")
    public ResponseEntity<List<BookDto>> getBookByAuthor(@PathVariable String authorLastName) {
        List<Book> allBooksByAuthorLastName = bookService.getBooksByAuthorLastName(authorLastName);
        if (allBooksByAuthorLastName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<BookDto> allBooksByAuthorDto = BookMapper.toResponseDtoList(allBooksByAuthorLastName);
        return ResponseEntity.ok(allBooksByAuthorDto);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@PathVariable String isbn) {
       Book bookByIsbn = bookService.getBookByISBN(isbn);
        BookDto bookByIsbnDto = BookMapper.toResponseDto(bookByIsbn);
        return ResponseEntity.ok(bookByIsbnDto);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BookDto>> getBookByCategory(@PathVariable BookCategory category) {
        List<Book> allBooksByCategoryDto = bookService.getBooksByCategory(category);
        List<BookDto> allBooksByCategoryDtoDto = BookMapper.toResponseDtoList(allBooksByCategoryDto);
        return ResponseEntity.ok(allBooksByCategoryDtoDto);
    }


    @PutMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBook(@Valid @PathVariable long bookId, @RequestBody BookInputDto bookInputDto) {
        Book book = this.bookService.updateBook(bookId, bookInputDto);
        BookDto bookDto = BookMapper.toResponseDto(book);
        return ResponseEntity.ok(bookDto);
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookDto> patchBook(@Valid @PathVariable long bookId, @RequestBody BookInputDto bookInputDto) {
        Book book = this.bookService.patchBook(bookId, bookInputDto);
        BookDto bookDto = BookMapper.toResponseDto(book);
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<BookDto> deleteBook(@Valid @PathVariable long bookId) {
        this.bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

}
