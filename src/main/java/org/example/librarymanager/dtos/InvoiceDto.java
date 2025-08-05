package org.example.librarymanager.dtos;


import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.models.User;


import java.time.LocalDate;
import java.util.List;


public class InvoiceDto {
    public Long invoiceId;
    public Long userId;
    public LocalDate invoiceDate;
    public String invoicePeriod;
    public Double invoiceAmount;
    public PaymentStatus paymentStatus;
    public List<FineDto> fines;
}
