package org.example.librarymanager.services;

import org.example.librarymanager.dtos.BookInputDto;
import org.example.librarymanager.exceptions.ResourceNotFountException;
import org.example.librarymanager.mappers.BookMapper;
import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;
import org.example.librarymanager.repositories.BookRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(BookInputDto bookInputDto) {
        Book book = BookMapper.toEntity(bookInputDto);
        return this.bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    public Book getBookById(Long bookId) {
        return this.bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFountException("Boek niet gevonden met ID: " + bookId));
    }

    public List<Book> getBooksByTitle(String title) {
        return this.bookRepository.findBookByTitle(title);
    }

    public List<Book> getBooksByAuthorLastName(String authorLastName) {
        return this.bookRepository.findBooksByAuthorLastName(authorLastName);
    }

    public Book getBookByISBN(String isbn) {
        return this.bookRepository.findBookByISBN(isbn)
                .orElseThrow(() -> new ResourceNotFountException("Boek niet gevonden met ISBN: " + isbn));
    }

    public List<Book> getBooksByCategory(BookCategory category) {
        return this.bookRepository.findBooksByCategory(category);
    }

    public Book updateBook(Long bookId, BookInputDto bookInputDto) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFountException("Boek niet gevonden met ID: " + bookId));

        existingBook.setTitle(bookInputDto.title);
        existingBook.setAuthorFirstName(bookInputDto.authorFirstName);
        existingBook.setAuthorLastName(bookInputDto.authorLastName);
        existingBook.setISBN(bookInputDto.ISBN);
        existingBook.setPublisher(bookInputDto.publisher);
        existingBook.setCategory(bookInputDto.category);

        return bookRepository.save(existingBook);
    }

    public Book patchBook(Long bookId, BookInputDto updates) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFountException("Boek niet gevonden met ID: " + bookId));

        if (updates.title != null) {
            existingBook.setTitle(updates.title);
        }
        if (updates.authorFirstName != null) {
            existingBook.setAuthorFirstName(updates.authorFirstName);
        }
        if (updates.authorLastName != null) {
            existingBook.setAuthorLastName(updates.authorLastName);
        }
        if (updates.publisher != null) {
            existingBook.setPublisher(updates.publisher);
        }
        if (updates.category != null) {
            existingBook.setCategory(updates.category);
        }

        return bookRepository.save(existingBook);
    }


    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

}
