package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.FineDto;
import org.example.librarymanager.models.Fine;

import java.util.List;
import java.util.stream.Collectors;

public class FineMapper {

    public static FineDto toResponseDto(Fine fine) {
        FineDto fineDto = new FineDto();
        fineDto.fineId = fine.getFineId();
        fineDto.fineAmount = fine.getFineAmount();
        fineDto.fineDate = fine.getFineDate();
        fineDto.isPaid = fine.getIsPaid();
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