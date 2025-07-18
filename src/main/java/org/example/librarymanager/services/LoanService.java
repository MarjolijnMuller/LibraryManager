package org.example.librarymanager.services;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.dtos.LoanPatchDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.LoanMapper;
import org.example.librarymanager.models.*;
import org.example.librarymanager.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;

    public LoanService(LoanRepository loanRepository, BookCopyRepository bookCopyRepository, UserRepository userRepository, FineRepository fineRepository) {
        this.loanRepository = loanRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.fineRepository = fineRepository;
    }

    public LoanDto createLoan(LoanInputDto loanInputDto) {
        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookcopy not found with ID: " + loanInputDto.bookCopyId));

        User user = userRepository.findById(loanInputDto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + loanInputDto.userId));

        if (loanRepository.findByBookCopyAndIsReturnedFalse(bookCopy).isPresent()) {
            throw new IllegalArgumentException("Bookcopy with ID " + bookCopy.getBookCopyId() + " has already been borrowed.");
        }

        Loan newLoan = LoanMapper.toEntity(loanInputDto, bookCopy, user);
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

    public List<LoanDto> getLoansByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return LoanMapper.toResponseDtoList(loanRepository.findByUser(user));
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

        LocalDate actualReturnDate = LocalDate.now();
        if (actualReturnDate.isAfter(loan.getReturnDate())) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(loan.getReturnDate(), actualReturnDate);
            Double fineAmount = overdueDays * 0.50;

            if (fineAmount > 0) {
                Fine newFine = new Fine();
                newFine.setFineAmount(fineAmount);
                newFine.setFineDate(actualReturnDate);
                newFine.setIsPaid(false);
                newFine.setLoan(loan);
                fineRepository.save(newFine);
            }
        }
        return LoanMapper.toResponseDto(loanRepository.save(loan));
    }

    public List<LoanDto> getLoansByBookCopyId(Long bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with ID: " + bookCopyId));
        return LoanMapper.toResponseDtoList(loanRepository.findByBookCopy(bookCopy));
    }

    public List<LoanDto> getOverdueLoans() {
        return LoanMapper.toResponseDtoList(loanRepository.findByReturnDateBeforeAndIsReturnedFalse(LocalDate.now()));
    }

    public List<LoanDto> getOutstandingLoans() {
        return LoanMapper.toResponseDtoList(loanRepository.findByIsReturnedFalse());
    }


    public LoanDto updateLoan(Long loanId, LoanInputDto loanInputDto) {
        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Lening niet gevonden met ID: " + loanId));

        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Boekexemplaar niet gevonden met ID: " + loanInputDto.bookCopyId));

        User user = userRepository.findById(loanInputDto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lid (User) niet gevonden met ID: " + loanInputDto.userId));

        existingLoan.setLoanDate(loanInputDto.loanDate);
        existingLoan.setReturnDate(loanInputDto.returnDate);
        existingLoan.setReturned(loanInputDto.isReturned);
        existingLoan.setBookCopy(bookCopy);
        existingLoan.setUser(user);

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
            User user = userRepository.findById(loanPatchDto.userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + loanPatchDto.userId));
            existingLoan.setUser(user);
        }

        return LoanMapper.toResponseDto(this.loanRepository.save(existingLoan));
    }

    public void deleteLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        BookCopy bookCopy = loan.getBookCopy();
        if (bookCopy != null && bookCopy.getStatus() == BookCopyStatus.ON_LOAN && !loan.isReturned()) {
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(bookCopy);
        }
        this.loanRepository.deleteById(loanId);
    }
}