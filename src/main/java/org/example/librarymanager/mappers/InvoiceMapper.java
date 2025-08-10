package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.InvoiceDto;
import org.example.librarymanager.dtos.InvoiceInputDto;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.PaymentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {
    public static Invoice toEntity(InvoiceInputDto invoiceInputDto){
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(invoiceInputDto.invoiceDate);
        invoice.setInvoicePeriod(invoiceInputDto.invoicePeriod);

        PaymentStatus defaultPaymentStatus = PaymentStatus.OPEN;

        if (invoiceInputDto.paymentStatus != null && !invoiceInputDto.paymentStatus.trim().isEmpty()) {
            defaultPaymentStatus = PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase());
            try {
                invoice.setPaymentStatus(PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid payment status: " + invoiceInputDto.paymentStatus + "'. Setting payment to open.");
                invoice.setPaymentStatus(defaultPaymentStatus);
            }
            invoice.setPaymentStatus(defaultPaymentStatus);
        }
        return invoice;
    }

    public static InvoiceDto toResponseDto(Invoice invoice){
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.invoiceId = invoice.getInvoiceId();
        invoiceDto.invoiceDate = invoice.getInvoiceDate();
        invoiceDto.invoicePeriod = invoice.getInvoicePeriod();
        invoiceDto.invoiceAmount = invoice.getInvoiceAmount();
        invoiceDto.paymentStatus = invoice.getPaymentStatus();

        if (invoice.getUser() != null) {
            invoiceDto.userId = invoice.getUser().getUserId();
        }

        if (invoice.getFines() != null && !invoice.getFines().isEmpty()) {
            invoiceDto.fines = invoice.getFines().stream()
                    .map(FineMapper::toResponseDto)
                    .collect(Collectors.toList());
        } else {
            invoiceDto.fines = new ArrayList<>();
        }
        return invoiceDto;
    }

    public static List<InvoiceDto> toResponseDtoList(List<Invoice> invoices){
        return invoices.stream()
                .map(InvoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
