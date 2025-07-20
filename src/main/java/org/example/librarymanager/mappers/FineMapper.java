package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.FineDto;
import org.example.librarymanager.dtos.FineInputDto;
import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.Loan;

import java.util.List;
import java.util.stream.Collectors;

public class FineMapper {
    public static Fine toEntity(FineInputDto fineInputDto, Invoice invoice, Loan loan) {
        Fine fine = new Fine();
        fine.setFineAmount(fineInputDto.fineAmount);
        fine.setFineDate(fineInputDto.fineDate);
        fine.setIsPaid(fineInputDto.isPaid);
        fine.setLoan(loan);
        fine.setInvoice(invoice);
        return fine;
    }

    public static FineDto toResponseDto(Fine fine) {
        FineDto fineDto = new FineDto();
        fineDto.fineId = fine.getFineId();
        fineDto.fineAmount = fine.getFineAmount();
        fineDto.fineDate = fine.getFineDate();
        fineDto.isPaid = fine.getIsPaid();
        fineDto.isReadyForInvoice = fine.getIsReadyForInvoice();

        if (fine.getLoan() != null) {
            fineDto.loanId = fine.getLoan().getLoanId();
        }
        if (fine.getInvoice() != null) {
            fineDto.invoiceId = fine.getInvoice().getInvoiceId();
        }
        return fineDto;
    }

    public static List<FineDto> toResponseDtoList(List<Fine> fines) {
        return fines.stream()
                .map(FineMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
