package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class FineInputDto {
    @NotNull
    @PositiveOrZero
    public Double fineAmount;

    @NotNull
    public LocalDate fineDate;
    public Boolean isPaid = false;

    @NotNull
    public Long loanId;
    @NotNull
    public Long invoiceId;
}
