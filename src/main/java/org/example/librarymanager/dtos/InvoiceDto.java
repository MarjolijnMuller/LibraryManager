package org.example.librarymanager.dtos;


import org.example.librarymanager.models.PaymentStatus;


import java.time.LocalDate;


public class InvoiceDto {
    public Long invoiceId;
    public LocalDate invoiceDate;
    public String invoicePeriod;
    public Double invoiceAmount;
    public PaymentStatus paymentStatus;
}
