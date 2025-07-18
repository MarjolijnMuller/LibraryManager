package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    Librarian findLibrarianByUserId(Long userId);
    List<Librarian> findByFirstName(String firstName);
    List<Librarian> findByLastName(String lastName);
    List<Librarian> findByFirstNameAndLastName(String firstName, String lastName);

    Librarian findByUsername(String username);
    List<Librarian> findByEmail(String email);
}
