package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanInputDto {
    @NotNull
    @Column(nullable = false)
    public LocalDate loanDate;

    @NotNull
    @Column(nullable = false)
    public LocalDate returnDate;

    @Column(nullable = false)
    public boolean isReturned = false;
}
