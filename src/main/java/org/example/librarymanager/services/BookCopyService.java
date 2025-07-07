package org.example.librarymanager.services;

import org.example.librarymanager.dtos.BookCopyDto;
import org.example.librarymanager.dtos.BookCopyInputDto;
import org.example.librarymanager.mappers.BookCopyMapper;
import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;
import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.BookCopyStatus;
import org.example.librarymanager.repositories.BookCopyRepository;
import org.example.librarymanager.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }


    public BookCopyDto createBookCopy(BookCopyInputDto bookCopyInputDto) {
        if (bookCopyInputDto.ISBN == null || bookCopyInputDto.ISBN.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN mag niet leeg zijn.");
        }

        Optional<Book> existingBookOptional = bookRepository.findBookByISBN(bookCopyInputDto.ISBN);
        Book book;

        if (existingBookOptional.isPresent()) {
            book = existingBookOptional.get();
        } else {
            if (bookCopyInputDto.title == null || bookCopyInputDto.title.trim().isEmpty() ||
                    bookCopyInputDto.authorFirstName == null || bookCopyInputDto.authorFirstName.trim().isEmpty() ||
                    bookCopyInputDto.authorLastName == null || bookCopyInputDto.authorLastName.trim().isEmpty() ||
                    bookCopyInputDto.category == null || bookCopyInputDto.category.trim().isEmpty()) {
                throw new IllegalArgumentException("Voor een nieuw boek zijn titel, auteur en categorie verplicht.");
            }

            BookCategory categoryEnum;
            try {
                categoryEnum = BookCategory.valueOf(bookCopyInputDto.category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ongeldige boekcategorie: " + bookCopyInputDto.category);
            }

            book = new Book(
                    bookCopyInputDto.title,
                    bookCopyInputDto.authorFirstName,
                    bookCopyInputDto.authorLastName,
                    bookCopyInputDto.ISBN,
                    bookCopyInputDto.publisher,
                    categoryEnum
            );
            book = bookRepository.save(book);
        }

        long nextFollowNumber;
        Optional<BookCopy> lastCopy = bookCopyRepository.findTopByBookOrderByFollowNumberDesc(book);

        if (lastCopy.isPresent()) {
            nextFollowNumber = lastCopy.get().getFollowNumber() + 1;
        } else {
            nextFollowNumber = 1L;
        }

        BookCopyStatus status = (bookCopyInputDto.status != null) ?
                bookCopyInputDto.status : BookCopyStatus.AVAILABLE;

        BookCopy newCopy = new BookCopy(nextFollowNumber, status, book);
        book.getCopies().add(newCopy);
        BookCopy savedCopy = this.bookCopyRepository.save(newCopy);

        return BookCopyMapper.toResponseDto(savedCopy);
    }

    public List<BookCopyDto> getAllBookCopiesForBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Boek niet gevonden met ID: " + bookId));
        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book);
        return BookCopyMapper.toDtoList(copies);
    }

    public BookCopyDto getBookCopyByBookIdAndFollowNumber(Long bookId, Long followNumber) {
        BookCopy bookCopy = bookCopyRepository.findByBook_BookIdAndFollowNumber(bookId, followNumber).orElseThrow(() -> new IllegalArgumentException("Boekenexemplaar niet gevonden voor boek ID: " + bookId + " en volgnummer: " + followNumber));
        return BookCopyMapper.toResponseDto(bookCopy);
    }

    public List<BookCopyDto> getAllBookCopiesByStatus(BookCopyStatus status) {
        List<BookCopy> copies = bookCopyRepository.findBookCopiesByStatus(status);
        return BookCopyMapper.toDtoList(copies);
    }

    public BookCopy updateBookCopy(Long bookId, Long followNumber, BookCopyInputDto bookCopyInputDto) {
        BookCopy existingBookCopy = bookCopyRepository.findByBook_BookIdAndFollowNumber(bookId, followNumber)
                .orElseThrow(() -> new IllegalArgumentException("Boekenexemplaar niet gevonden voor boek ID: " + bookId + " en volgnummer: " + followNumber));
        if (bookCopyInputDto.status != null) {
            existingBookCopy.setStatus(bookCopyInputDto.status);
        } else {
            throw new IllegalArgumentException("Status is verplicht voor een volledige update (PUT) van een boekenexemplaar.");
        }
        return bookCopyRepository.save(existingBookCopy);
    }

    //TODO: patch

    public void deleteBookCopy(Long bookId, Long followNumber) {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByBook_BookIdAndFollowNumber(bookId, followNumber);

        if (bookCopyOptional.isPresent()) {
            BookCopy bookCopyToDelete = bookCopyOptional.get();
            bookCopyRepository.delete(bookCopyToDelete);
        } else {
            throw new IllegalArgumentException("Boekenexemplaar niet gevonden voor boek ID: " + bookId + " en volgnummer: " + followNumber);
        }
    }
}