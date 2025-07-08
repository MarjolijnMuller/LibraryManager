package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanDto {
    public Long loanId;
    public LocalDate loanDate;
    public LocalDate returnDate;
    public boolean isReturned;
    public Long bookCopyId;
    public Long memberId;
    public Long fineId;
}
