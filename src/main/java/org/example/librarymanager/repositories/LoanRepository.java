package org.example.librarymanager.repositories;

import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    List<Loan> findByUser_UserId(Long userId);

    List<Loan> findByBookCopy(BookCopy bookCopy);

    List<Loan> findByIsReturnedFalse();

    List<Loan> findByReturnDateBeforeAndIsReturnedFalse(LocalDate currentDate);

    Optional<Loan> findByBookCopyAndIsReturnedFalse(BookCopy bookCopy);
}
