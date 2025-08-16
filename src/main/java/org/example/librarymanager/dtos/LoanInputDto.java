package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanInputDto {
    @NotNull
    public LocalDate loanDate;

    @NotNull
    public LocalDate returnDate;

    private LocalDate actualReturnDate;

    @NotNull
    public Boolean isReturned = false;

    @NotNull
    public Long bookCopyId;

    @NotNull
    public Long userId;
    public Long fineId;
}
