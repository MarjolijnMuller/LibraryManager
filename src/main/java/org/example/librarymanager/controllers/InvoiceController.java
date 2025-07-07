package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.BookCopyDto;
import org.example.librarymanager.dtos.InvoiceDto;
import org.example.librarymanager.dtos.InvoiceInputDto;
import org.example.librarymanager.mappers.InvoiceMapper;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.services.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(final InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice invoice = this.invoiceService.createInvoice(invoiceInputDto);
        InvoiceDto invoiceDto = InvoiceMapper.toResponseDto(invoice);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + invoiceDto.invoiceId)
                        .toUriString());
        return ResponseEntity.created(uri).body(invoiceDto);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        List<Invoice> allInvoices = invoiceService.getAllInvoices();
        List<InvoiceDto> allInvoiceDtos = InvoiceMapper.toResponseDtoList(allInvoices);
        return ResponseEntity.ok(allInvoiceDtos);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDto(invoiceService.getInvoiceById(invoiceId)));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoiceService.getInvoicesByDate(date)));
    }

    @GetMapping("/date/before/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByDateBefore(@PathVariable LocalDate date) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoiceService.getInvoicesBeforeDate(date)));
    }

    @GetMapping("/date/after/{date}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByDateAfter(@PathVariable LocalDate date) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoiceService.getInvoicesAfterDate(date)));
    }

    @GetMapping("/date/between/{startDate}/{endDate}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByDateBetween(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoiceService.getInvoicesByDateRangeSorted(startDate, endDate)));
    }

    @GetMapping("/period/{period}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByPeriod(@PathVariable String period) {
        return ResponseEntity.ok(InvoiceMapper.toResponseDtoList(invoiceService.getInvoicesByInvoicePeriod(period)));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByPaymentStatus(@PathVariable String status) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByPaymentStatus(PaymentStatus.valueOf(status.toUpperCase()));

            List<InvoiceDto> invoiceDtos = InvoiceMapper.toResponseDtoList(invoices);

            return ResponseEntity.ok(invoiceDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> updateInvoice(@Valid @PathVariable Long invoiceId, @RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice invoice = this.invoiceService.updateInvoice(invoiceId, invoiceInputDto);
        InvoiceDto invoiceDto = InvoiceMapper.toResponseDto(invoice);
        return ResponseEntity.ok(invoiceDto);
    }

    @PatchMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> patchInvoice(@Valid @PathVariable Long invoiceId, @RequestBody InvoiceInputDto invoiceInputDto) {
        Invoice invoice = this.invoiceService.patchInvoice(invoiceId, invoiceInputDto);
        InvoiceDto invoiceDto = InvoiceMapper.toResponseDto(invoice);
        return ResponseEntity.ok(invoiceDto);
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}
