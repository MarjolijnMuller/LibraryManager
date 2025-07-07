package org.example.librarymanager.services;

import org.example.librarymanager.dtos.InvoiceDto;
import org.example.librarymanager.dtos.InvoiceInputDto;
import org.example.librarymanager.dtos.LibrarianInputDto;
import org.example.librarymanager.exceptions.ResourceNotFountException;
import org.example.librarymanager.mappers.InvoiceMapper;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.repositories.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }


    public Invoice createInvoice(InvoiceInputDto invoieInputDto){
        return this.invoiceRepository.save(InvoiceMapper.toEntity(invoieInputDto));
    }

    public List<Invoice> getAllInvoices(){
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long invoiceId){
        return this.invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFountException("Invoice not found with ID: " + invoiceId));
    }

    public List<Invoice> getInvoicesByDate(LocalDate date){
        return this.invoiceRepository.findByInvoiceDate(date);
    }

    public List<Invoice> getInvoicesBeforeDate(LocalDate date){
        return this.invoiceRepository.findByInvoiceDateBeforeOrderByInvoiceDateAsc(date);
    }

    public List<Invoice> getInvoicesAfterDate(LocalDate date){
        return this.invoiceRepository.findByInvoiceDateAfterOrderByInvoiceDateDesc(date);
    }

    public List<Invoice> getInvoicesByDateRangeSorted (LocalDate startDate, LocalDate endDate){
        return this.invoiceRepository.findInvoicesByDateRangeSorted(startDate, endDate);
    }

    public List<Invoice> getInvoicesByInvoicePeriod(String invoicePeriod){
        return this.invoiceRepository.findByInvoicePeriod(invoicePeriod);
    }

    public List<Invoice> getInvoicesByPaymentStatus(PaymentStatus paymentStatus){
        return this.invoiceRepository.findByPaymentStatus(paymentStatus);
    }

    public Invoice updateInvoice(Long invoiceId, InvoiceInputDto invoiceInputDto){
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFountException("Invoice not found with ID: " + invoiceId));

        if (invoiceInputDto.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(invoiceInputDto.invoicePeriod);
        }

        existingInvoice.setInvoiceDate(invoiceInputDto.invoiceDate);
        existingInvoice.setInvoiceAmount(invoiceInputDto.invoiceAmount);

        if(invoiceInputDto.paymentStatus != null && !invoiceInputDto.paymentStatus.trim().isEmpty()){
            try {
                existingInvoice.setPaymentStatus(PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment status " + invoiceInputDto.paymentStatus);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    public Invoice patchInvoice(Long invoiceId, InvoiceInputDto updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFountException("Invoice not found with ID: " + invoiceId));

        if (updates.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(updates.invoicePeriod);
        }
        if (updates.invoiceDate != null) {
            existingInvoice.setInvoiceDate(updates.invoiceDate);
        }
        if (updates.invoiceAmount != null) {
            existingInvoice.setInvoiceAmount(updates.invoiceAmount);
        }
        if (updates.paymentStatus != null) {
            try {
                existingInvoice.setPaymentStatus(PaymentStatus.valueOf(updates.paymentStatus.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status of payment for patch: " + updates.paymentStatus, e);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    public void deleteInvoice(Long invoiceId){
        this.invoiceRepository.deleteById(invoiceId);
    }
}
