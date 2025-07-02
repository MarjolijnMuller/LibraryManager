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

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookInputDto bookInputDto) {
        Book book = this.bookService.createBook(bookInputDto);
        BookDto bookDto = BookMapper.toResponseDto(book);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + bookDto.bookId).toUriString()
        );

        return ResponseEntity.created(uri).body(bookDto);
    }


    @GetMapping // Mapt HTTP GET-verzoeken naar /books
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<Book> allBooks = bookService.getAllBooks();
        List<BookDto> allBooksDto = BookMapper.toResponseDtoList(allBooks);
        return ResponseEntity.ok(allBooksDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable long id) {
        return ResponseEntity.ok(BookMapper.toResponseDto(bookService.getBookById(id)));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookDto>> getBookByTitle(@PathVariable String title) {
        List<Book> allBooksByTitle = bookService.getBooksByTitle(title);
        List<BookDto> allBooksByTitleDto = BookMapper.toResponseDtoList(allBooksByTitle);
        return ResponseEntity.ok(allBooksByTitleDto);
    }

    @GetMapping("/author/{authorLastName}")
    public ResponseEntity<List<BookDto>> getBookByAuthor(@PathVariable String authorLastName) {
        List<Book> allBooksByAuthorLastName = bookService.getBooksByAuthorLastName(authorLastName);
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


    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@Valid @PathVariable long id, @RequestBody BookInputDto bookInputDto) {
        Book book = this.bookService.updateBook(id, bookInputDto);
        BookDto bookDto = BookMapper.toResponseDto(book);
        return ResponseEntity.ok(bookDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDto> patchBook(@Valid @PathVariable long id, @RequestBody BookInputDto bookInputDto) {
        Book book = this.bookService.patchBook(id, bookInputDto);
        BookDto bookDto = BookMapper.toResponseDto(book);
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDto> deleteBook(@Valid @PathVariable long id) {
        this.bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
