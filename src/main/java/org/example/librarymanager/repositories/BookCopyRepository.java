package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findTopByBookOrderByFollowNumberDesc(Book book);

    long countByBook(Book book);

    List<BookCopy> findBookCopiesByStatus(BookCopyStatus status);

    List<BookCopy> findBookCopiesByBook(Book book);

    Optional<BookCopy> findByBook_BookIdAndFollowNumber(Long bookId, Long followNumber);
}
