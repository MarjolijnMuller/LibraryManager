package org.example.librarymanager.dtos;

import java.time.LocalDate;

public class FineDto {
    public long fineId;
    public Double fineAmount;
    public LocalDate fineDate;
    public Boolean isPaid = false;
    public Long loanId;
}
