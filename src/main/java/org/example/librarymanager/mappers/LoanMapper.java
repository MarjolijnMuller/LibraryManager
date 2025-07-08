package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.models.Loan;

import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {
    public static Loan toEntity(LoanInputDto loanInputDto) {
        Loan loan = new Loan();
        loan.setLoanDate(loanInputDto.loanDate);
        loan.setReturnDate(loanInputDto.returnDate);
        loan.setReturned(loanInputDto.isReturned);
        return loan;
    }

    public static LoanDto toResponseDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        loanDto.loanId = loan.getLoanId();
        loanDto.loanDate = loan.getLoanDate();
        loanDto.returnDate = loan.getReturnDate();
        loanDto.isReturned = loan.isReturned();
        return loanDto;
    }

    public static List<LoanDto> toResponseDtoList(List<Loan> loans) {
        return loans.stream()
                .map(LoanMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
