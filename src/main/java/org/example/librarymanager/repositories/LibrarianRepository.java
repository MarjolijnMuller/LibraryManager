package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    Librarian findLibrarianByLibrarianId(Long librarianId);
    List<Librarian> findByFirstName(String firstName);
    List<Librarian> findByLastName(String lastName);
    Librarian findByFirstNameAndLastName(String firstName, String lastName);

    Librarian findByUsername(String username);
    List<Librarian> findByEmail(String email);
}
