package org.example.librarymanager.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InvoiceInputDto {
    @NotNull
    public LocalDate invoiceDate;
    public String invoicePeriod;
    public String paymentStatus;
    public Long userId;
}