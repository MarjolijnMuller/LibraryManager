package org.example.librarymanager.services;

import org.example.librarymanager.config.FineConfiguration;
import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.dtos.LoanPatchDto;
import org.example.librarymanager.exceptions.NotEnoughCopiesException;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.LoanMapper;
import org.example.librarymanager.models.*;
import org.example.librarymanager.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;
    private final FineConfigurationRepository fineConfigurationRepository;


    public LoanService(LoanRepository loanRepository, BookCopyRepository bookCopyRepository, UserRepository userRepository, FineRepository fineRepository, FineConfigurationRepository fineConfigurationRepository) {
        this.loanRepository = loanRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.fineRepository = fineRepository;
        this.fineConfigurationRepository = fineConfigurationRepository;
    }

    public LoanDto createLoan(LoanInputDto loanInputDto) {
        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookcopy not found with ID: " + loanInputDto.bookCopyId));

        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new NotEnoughCopiesException("This book copy is not available for loan.");
        }

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
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getIsReturned()) {
            throw new IllegalStateException("The book copy has already been returned.");
        }

        loan.setIsReturned(true);
        loan.setActualReturnDate(LocalDate.now());
        Loan savedLoan = loanRepository.save(loan);

        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        Fine fine = calculateAndSaveFine(savedLoan);
        if (fine != null) {
            savedLoan.getFines().add(fine);
        }
        return LoanMapper.toResponseDto(savedLoan);
    }

    public Fine calculateAndSaveFine(Loan loan) {
        FineConfiguration config = fineConfigurationRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Fine configuration not found! Make sure it is set up in the database."));

        LocalDate dueDate = loan.getReturnDate();
        LocalDate calculationDate = loan.getActualReturnDate() != null ? loan.getActualReturnDate() : LocalDate.now();

        if (calculationDate.isAfter(dueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(dueDate, calculationDate);
            double calculatedFineAmount = overdueDays * config.getDailyFine();

            if (calculatedFineAmount > config.getMaxFineAmount()) {
                calculatedFineAmount = config.getMaxFineAmount();
            }

            Optional<Fine> existingFineOptional = loan.getFines().stream().findFirst();
            Fine fine = existingFineOptional.orElseGet(Fine::new);

            fine.setLoan(loan);
            fine.setFineAmount(calculatedFineAmount);
            fine.setFineDate(LocalDate.now());
            fine.setIsPaid(false);
            fine.setInvoice(null);

            fine.setIsReadyForInvoice(loan.getActualReturnDate() != null || calculatedFineAmount >= config.getMaxFineAmount());

            return fineRepository.save(fine);
        } else {
            loan.getFines().stream().findFirst().ifPresent(fineRepository::delete);
            return null;
        }
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
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        BookCopy bookCopy = bookCopyRepository.findById(loanInputDto.bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with ID: " + loanInputDto.bookCopyId));

        User user = userRepository.findById(loanInputDto.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + loanInputDto.userId));

        boolean wasReturnedBeforeUpdate = existingLoan.getIsReturned();

        existingLoan.setLoanDate(loanInputDto.loanDate);
        existingLoan.setReturnDate(loanInputDto.returnDate);
        existingLoan.setIsReturned(loanInputDto.isReturned);
        existingLoan.setBookCopy(bookCopy);
        existingLoan.setUser(user);

        if (loanInputDto.isReturned != null && loanInputDto.isReturned && !wasReturnedBeforeUpdate) {
            existingLoan.setIsReturned(true);
            existingLoan.setActualReturnDate(LocalDate.now());
        } else if (loanInputDto.isReturned != null && !loanInputDto.isReturned && wasReturnedBeforeUpdate) {

            existingLoan.setIsReturned(false);
            existingLoan.setActualReturnDate(null);
            existingLoan.getFines().stream().findFirst().ifPresent(fineRepository::delete);
        } else if (loanInputDto.isReturned != null) {
            existingLoan.setIsReturned(loanInputDto.isReturned);
        }

        Loan savedLoan = loanRepository.save(existingLoan);

        if (savedLoan.getIsReturned()) {
            calculateAndSaveFine(savedLoan);
        } else if (savedLoan.getReturnDate() != null && savedLoan.getReturnDate().isBefore(LocalDate.now())) {
            calculateAndSaveFine(savedLoan);
        }

        return LoanMapper.toResponseDto(loanRepository.save(existingLoan));
    }

    public void updateOverdueFines() {
        List<Loan> overdueLoans = loanRepository.findByReturnDateBeforeAndIsReturnedFalse(LocalDate.now());
        for (Loan loan : overdueLoans) {
            Optional<Fine> existingFine = loan.getFines().stream().findFirst();
            if (existingFine.isPresent()) {
                if (!existingFine.get().getIsReadyForInvoice()) {
                    calculateAndSaveFine(loan);
                }
            } else {
                calculateAndSaveFine(loan);
            }
        }
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
            existingLoan.setIsReturned(loanPatchDto.isReturned);
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
        if (bookCopy != null && bookCopy.getStatus() == BookCopyStatus.ON_LOAN && !loan.getIsReturned()) {
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopyRepository.save(bookCopy);
        }
        this.loanRepository.deleteById(loanId);
    }
}