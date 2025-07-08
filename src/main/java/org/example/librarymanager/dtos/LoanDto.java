package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanDto {
    public Long loanId;
    public LocalDate date;
    public LocalDate returnDate;
    public boolean isReturned = false;
}
