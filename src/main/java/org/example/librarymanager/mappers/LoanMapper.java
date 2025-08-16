package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {
    public static Loan toEntity(LoanInputDto loanInputDto, BookCopy bookCopy, User user) {
        Loan loan = new Loan();
        loan.setLoanDate(loanInputDto.loanDate);
        loan.setReturnDate(loanInputDto.returnDate);
        loan.setIsReturned(false);
        loan.setBookCopy(bookCopy);
        loan.setUser(user);
        loan.setFines(null);
        return loan;
    }

    public static LoanDto toResponseDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        loanDto.loanId = loan.getLoanId();
        loanDto.loanDate = loan.getLoanDate();
        loanDto.returnDate = loan.getReturnDate();
        loanDto.isReturned = loan.getIsReturned();
        loanDto.actualReturnDate = loan.getActualReturnDate();

        if (loan.getBookCopy() != null) {
            loanDto.bookCopyId = loan.getBookCopy().getBookCopyId();
        }
        if (loan.getUser() != null) {
            loanDto.userId = loan.getUser().getUserId();
        }
        if (loan.getFines() != null && !loan.getFines().isEmpty()) {
            loanDto.fineId = loan.getFines().get(0).getFineId();
        }

        loanDto.status = determineLoanStatus(loan);

        return loanDto;
    }

    public static List<LoanDto> toResponseDtoList(List<Loan> loans) {
        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private static String determineLoanStatus(Loan loan) {
        if (loan.getIsReturned()) {
            return "Returned";
        } else {
            if (loan.getReturnDate() != null && loan.getReturnDate().isBefore(LocalDate.now())) {
                return "Overdue";
            } else {
                return "Outstanding";
            }
        }
    }
}