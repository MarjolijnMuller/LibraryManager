package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class FinePatchDto {
    public Double fineAmount;
    public LocalDate fineDate;
    public Boolean isPaid = false;
    public Long loanId;
    public Long invoiceId;
}
