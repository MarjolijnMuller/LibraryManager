package org.example.librarymanager.dtos;

import java.time.LocalDate;

public class FinePatchDto {
    public Double fineAmount;
    public LocalDate fineDate;
    public Boolean isPaid = false;
    public Long loanId;
    public Long invoiceId;
}
