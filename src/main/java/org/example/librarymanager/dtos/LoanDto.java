package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanDto {
    public Long loanId;
    public LocalDate loanDate;
    public LocalDate returnDate;
    public Boolean isReturned;
    public Long bookCopyId;
    public Long userId;
    public Long fineId;
    public String status;
}
