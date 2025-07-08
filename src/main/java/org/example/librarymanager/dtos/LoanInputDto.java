package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanInputDto {
    @NotNull
    public LocalDate loanDate;
    @NotNull
    public LocalDate returnDate;
    @NotNull
    public boolean isReturned;
    @NotNull
    public Long bookCopyId;
    @NotNull
    public Long userId;
}
