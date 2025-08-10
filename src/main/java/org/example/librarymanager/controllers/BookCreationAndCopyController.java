package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.BookCopyDto;
import org.example.librarymanager.dtos.BookCopyInputDto;
import org.example.librarymanager.mappers.BookCopyMapper;
import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.services.BookCopyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookCreationAndCopyController {

    private final BookCopyService bookCopyService;

    public BookCreationAndCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping
    public ResponseEntity<BookCopyDto> createBookWithFirstCopy(@Valid @RequestBody BookCopyInputDto bookCopyInputDto) {
        BookCopyDto createdCopyDto = bookCopyService.createBookCopy(bookCopyInputDto);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .path("/copies/" + createdCopyDto.bookCopyId).toUriString()
        );

        return ResponseEntity.created(uri).body(createdCopyDto);
    }

    @GetMapping("/{bookId}/copies")
    public ResponseEntity<List<BookCopyDto>> getAllCopiesForBook(@PathVariable Long bookId) {
        List<BookCopyDto> copies = bookCopyService.getAllBookCopiesForBook(bookId);
        return ResponseEntity.ok(copies);
    }

    @GetMapping("/{bookId}/copies/{followNumber}")
    public ResponseEntity<BookCopyDto> getBookCopyByBookIdAndFollowNumber(@PathVariable Long bookId, @PathVariable Long followNumber) {
        BookCopyDto copy = bookCopyService.getBookCopyByBookIdAndFollowNumber(bookId, followNumber);
        return ResponseEntity.ok(copy);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookCopyDto>> getBookCopiesByStatus(@PathVariable String status) {
        try {
            List<BookCopyDto> copies = bookCopyService.getAllBookCopiesByStatus(org.example.librarymanager.models.BookCopyStatus.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(copies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{bookId}/copies/{followNumber}")
    public ResponseEntity<BookCopyDto> updateBookCopy(
            @PathVariable Long bookId,
            @PathVariable Long followNumber,
            @RequestBody BookCopyInputDto bookCopyInputDto) {
        BookCopy updatedBookCopy = bookCopyService.updateBookCopy(bookId, followNumber, bookCopyInputDto);
        return ResponseEntity.ok(BookCopyMapper.toResponseDto(updatedBookCopy));
    }


    @DeleteMapping("/{bookId}/copies/{followNumber}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long bookId, @PathVariable Long followNumber) {
        bookCopyService.deleteBookCopy(bookId, followNumber);
        return ResponseEntity.noContent().build();
    }
}
