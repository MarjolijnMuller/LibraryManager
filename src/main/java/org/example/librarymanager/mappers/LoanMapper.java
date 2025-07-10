package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.models.BookCopy;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.models.Member;

import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {
    //TODO: voeg Fine toe tussen de () en loan.setFine(null)
    public static Loan toEntity(LoanInputDto loanInputDto, BookCopy bookCopy, Member member) {
        Loan loan = new Loan();
        loan.setLoanDate(loanInputDto.loanDate);
        loan.setReturnDate(loanInputDto.returnDate);
        loan.setReturned(false);
        loan.setBookCopy(bookCopy);
        loan.setMember(member);
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
        if (loan.getMember() != null) {
            loanDto.userId = loan.getMember().getUserId();
        }

        return loanDto;
    }

    public static List<LoanDto> toResponseDtoList(List<Loan> loans) {
        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
