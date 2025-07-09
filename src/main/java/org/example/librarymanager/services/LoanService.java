package org.example.librarymanager.services;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.dtos.LoanPatchDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.LoanMapper;
import org.example.librarymanager.models.*;
import org.example.librarymanager.repositories.BookCopyRepository;
import org.example.librarymanager.repositories.FineRepository;
import org.example.librarymanager.repositories.LoanRepository;
import org.example.librarymanager.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository, BookCopyRepository bookCopyRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.memberRepository = memberRepository;
    }

    public LoanDto createLoan(LoanInputDto loanInputDto) {
        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookcopy not found with ID: " + loanInputDto.bookCopyId));

        Member member = memberRepository.findById(loanInputDto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + loanInputDto.userId));

        if (loanRepository.findByBookCopyAndIsReturnedFalse(bookCopy).isPresent()) {
            throw new IllegalArgumentException("Bookcopy with ID " + bookCopy.getBookCopyId() + " has already been borrowed.");
        }

        Loan newLoan = LoanMapper.toEntity(loanInputDto, bookCopy, member);
        Loan savedLoan = loanRepository.save(newLoan);
        bookCopy.setStatus(BookCopyStatus.ON_LOAN);
        bookCopyRepository.save(bookCopy);

        return LoanMapper.toResponseDto(savedLoan);
    }

    public List<LoanDto> getAllLoans() {
        return LoanMapper.toResponseDtoList(loanRepository.findAll());
    }

    public LoanDto getLoanById(Long loanId) {
        return LoanMapper.toResponseDto(loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId)));
    }

    public List<LoanDto> getLoansByMemberId(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));
        return LoanMapper.toResponseDtoList(loanRepository.findByMember(member));

    }

    public LoanDto returnBookCopy(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        if (loan.isReturned()) {
            throw new IllegalArgumentException("Loan already returned.");
        }

        loan.setReturned(true);
        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        //TODO: berekening boete (let op het liefst per boek per dag
        LocalDate actualReturnDate = LocalDate.now(); // Of de datum die de gebruiker meegeeft
        if (actualReturnDate.isAfter(loan.getReturnDate())) {
            // Dit is een simpele berekening, pas dit aan naar je eigen regels
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(loan.getReturnDate(), actualReturnDate);
            double fineAmount = overdueDays * 0.50; // Bijvoorbeeld €0.50 per dag te laat

            if (fineAmount > 0) {
                Fine newFine = new Fine(fineAmount, actualReturnDate, false, loan);
                loan.setFine(newFine); // Zorgt ervoor dat de OneToOne relatie in Loan wordt gezet
                fineRepository.save(newFine); // Sla de boete expliciet op
            }
        }
        return LoanMapper.toResponseDto(loanRepository.save(loan));
    }

    //TODO: meer get-functies?

    public LoanDto updateLoan(Long loanId, LoanInputDto loanInputDto) {
        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Lening niet gevonden met ID: " + loanId));

        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Boekexemplaar niet gevonden met ID: " + loanInputDto.bookCopyId));

        Member member = memberRepository.findById(loanInputDto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lid niet gevonden met ID: " + loanInputDto.userId));

        existingLoan.setLoanDate(loanInputDto.loanDate);
        existingLoan.setReturnDate(loanInputDto.returnDate);
        existingLoan.setReturned(loanInputDto.isReturned);
        existingLoan.setBookCopy(bookCopy);
        existingLoan.setMember(member);

        return LoanMapper.toResponseDto(loanRepository.save(existingLoan));
    }

    public LoanDto patchLoan(Long loanId, LoanPatchDto loanPatchDto) {
        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        if (loanPatchDto.loanDate != null) {
            existingLoan.setLoanDate(loanPatchDto.loanDate);
        }
        if (loanPatchDto.returnDate != null) {
            existingLoan.setReturnDate(loanPatchDto.returnDate);
        }

        if (loanPatchDto.isReturned != null) {
            existingLoan.setReturned(loanPatchDto.isReturned);
        }

        if (loanPatchDto.bookCopyId != null) {
            BookCopy bookCopy = bookCopyRepository.findById(loanPatchDto.bookCopyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with ID: " + loanPatchDto.bookCopyId));
            existingLoan.setBookCopy(bookCopy);
        }

        if (loanPatchDto.userId != null) {
            Member member = memberRepository.findById(loanPatchDto.userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + loanPatchDto.userId));
            existingLoan.setMember(member);
        }

        return LoanMapper.toResponseDto(this.loanRepository.save(existingLoan));
    }

    public void deleteLoan(Long loanId) {
        this.loanRepository.deleteById(loanId);
    }
}
