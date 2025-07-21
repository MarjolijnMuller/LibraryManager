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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice createdInvoice = invoiceService.createInvoice(invoiceInputDto);
        return new ResponseEntity<>(InvoiceMapper.toResponseDto(createdInvoice), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
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

    @GetMapping("/by-date")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByDate(@RequestParam("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesByDate(date);
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/before-date")
    public ResponseEntity<List<InvoiceDto>> getInvoicesBeforeDate(@RequestParam("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesBeforeDate(date);
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/after-date")
    public ResponseEntity<List<InvoiceDto>> getInvoicesAfterDate(@RequestParam("date") LocalDate date) {
        List<Invoice> invoices = invoiceService.getInvoicesAfterDate(date);
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByDateRangeSorted(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        List<Invoice> invoices = invoiceService.getInvoicesByDateRangeSorted(startDate, endDate);
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/by-period")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByInvoicePeriod(@RequestParam("period") String invoicePeriod) {
        List<Invoice> invoices = invoiceService.getInvoicesByInvoicePeriod(invoicePeriod);
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByPaymentStatus(@RequestParam("status") String paymentStatusString) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentStatusString.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByPaymentStatus(paymentStatus);
            return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoices));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> updateInvoice(@PathVariable("id") Long id, @RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice updatedInvoice = invoiceService.updateInvoice(id, invoiceInputDto);
        return ResponseEntity