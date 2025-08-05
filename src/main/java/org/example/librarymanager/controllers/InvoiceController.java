package org.example.librarymanager.controllers;

import org.example.librarymanager.dtos.InvoiceDto;
import org.example.librarymanager.dtos.InvoiceInputDto;
import org.example.librarymanager.mappers.InvoiceMapper;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.services.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        if (invoices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable("id") Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(InvoiceMapper.toResponseDto(invoice));
    }

    @GetMapping("/generate")
    public ResponseEntity<List<InvoiceDto>> generateInvoices() {
        List<Invoice> generatedInvoices = invoiceService.generateInvoicesForReadyFines();
        if (generatedInvoices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(generatedInvoices));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<InvoiceDto> processPayment(@PathVariable("id") Long id) {
        Invoice updatedInvoice = invoiceService.processInvoicePayment(id);
        return ResponseEntity.ok(InvoiceMapper.toResponseDto(updatedInvoice));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByDate(@PathVariable("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesByDate(date);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/date/before/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesBeforeDate(@PathVariable("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesBeforeDate(date);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/date/after/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesAfterDate(@PathVariable("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesAfterDate(date);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/period/{invoicePeriod}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByInvoicePeriod(@PathVariable("invoicePeriod") String invoicePeriod) {
        List<Invoice> invoices = invoiceService.getInvoicesByInvoicePeriod(invoicePeriod);
        if (invoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByPaymentStatus(@PathVariable("status") String paymentStatusString) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentStatusString.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByPaymentStatus(paymentStatus);
            if (invoices.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> updateInvoice(@PathVariable("id") Long id, @RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice updatedInvoice = invoiceService.updateInvoice(id, invoiceInputDto);
        return ResponseEntity.ok(InvoiceMapper.toResponseDto(updatedInvoice));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InvoiceDto> patchInvoice(@PathVariable("id") Long id, @RequestBody InvoiceInputDto updates) {
        Invoice patchedInvoice = invoiceService.patchInvoice(id, updates);
        return ResponseEntity.ok(InvoiceMapper.toResponseDto(patchedInvoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}