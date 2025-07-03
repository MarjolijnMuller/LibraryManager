package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBookByTitle(String title);

    List<Book> findBooksByAuthorLastName(String authorLastName);

    Optional<Book> findBookByISBN(String ISBN);

    List<Book> findBooksByCategory(BookCategory category);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.copies WHERE b.bookId = :bookId")
    Optional<Book> findBookWithCopiesById(@Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.copies")
    List<Book> findAllWithCopies();
}
