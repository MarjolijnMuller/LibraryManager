package org.example.librarymanager.repositories;

import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.models.Member;
import org.example.librarymanager.models.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    List<Loan> findByMember(Member member);
    List<Loan> findByMemberUserId(Long memberId);

    List<Loan> findByBookCopy(BookCopy bookCopy);
    Optional<Loan> findByBookCopyBookCopyId(Long bookCopyId);

    List<Loan> findByIsReturnedFalse();

    List<Loan> findByReturnDateBeforeAndIsReturnedFalse(LocalDate currentDate);

    Optional<Loan> findByBookCopyAndIsReturnedTrue(BookCopy bookCopy);
}
