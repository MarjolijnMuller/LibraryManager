package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class FineInputDto {
    @NotNull
    @PositiveOrZero
    public Double amount;

    @NotNull
    @PositiveOrZero
    public Integer overdueDays;
}
