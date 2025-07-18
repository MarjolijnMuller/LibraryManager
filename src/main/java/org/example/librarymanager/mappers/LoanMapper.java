package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.models.UserInformation;

import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {
    public static Loan toEntity(LoanInputDto loanInputDto, BookCopy bookCopy, UserInformation userInformation) {
        Loan loan = new Loan();
        loan.setLoanDate(loanInputDto.loanDate);
        loan.setReturnDate(loanInputDto.returnDate);
        loan.setReturned(false);
        loan.setBookCopy(bookCopy);
        loan.setUserInformation(userInformation);
        loan.setFines(null);
        return loan;
    }

    public static LoanDto toResponseDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        loanDto.loanId = loan.getLoanId();
        loanDto.loanDate = loan.getLoanDate();
        loanDto.returnDate = loan.getReturnDate();
        loanDto.isReturned = loan.isReturned();

        if (loan.getBookCopy() != null) {
            loanDto.bookCopyId = loan.getBookCopy().getBookCopyId();
        }
        if (loan.getUserInformation() != null) {
            loanDto.userId = loan.getUserInformation().getUser().getUserId();
        }
        if (loan.getFines() != null && !loan.getFines().isEmpty()) {
            loanDto.fineId = loan.getFines().get(0).getFineId();
        }
        return loanDto;
    }

    public static List<LoanDto> toResponseDtoList(List<Loan> loans) {
        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}