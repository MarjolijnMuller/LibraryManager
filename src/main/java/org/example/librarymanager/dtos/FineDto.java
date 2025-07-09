package org.example.librarymanager.dtos;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class FineDto {
    public long fineId;
    public Double fineAmount;
    public LocalDate fineDate;
    public Boolean isPaid = false;
    public Long loanId;
    public Long invoiceId;
}
