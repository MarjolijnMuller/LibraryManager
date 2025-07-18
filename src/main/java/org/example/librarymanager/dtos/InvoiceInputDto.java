package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.example.librarymanager.models.PaymentStatus;

import java.time.LocalDate;

public class InvoiceInputDto {
    @NotNull
    public LocalDate invoiceDate;

    @Size(min=3, max=250)
    public String invoicePeriod;

    @NotNull
    @PositiveOrZero
    public Double invoiceAmount;

    @NotNull
    public String paymentStatus;
}
