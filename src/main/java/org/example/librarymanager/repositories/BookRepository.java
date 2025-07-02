package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Book;
import org.example.librarymanager.models.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByTitle(String title);

    List<Book> findBooksByAuthorLastName(String authorLastName);

    Optional<Book> findBookByISBN(String isbn);

    List<Book> findBooksByCategory(BookCategory category);
}
