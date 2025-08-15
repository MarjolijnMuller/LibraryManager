package org.example.librarymanager.services;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.librarymanager.dtos.InvoiceInputDto;
import org.example.librarymanager.exceptions.InvoiceAlreadyPaidException;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.InvoiceMapper;
import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.models.User;
import org.example.librarymanager.repositories.FineRepository;
import org.example.librarymanager.repositories.InvoiceRepository;
import org.example.librarymanager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final FineRepository fineRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;

    public InvoiceService(InvoiceRepository invoiceRepository, FineRepository fineRepository, UserRepository userRepository, PdfService pdfService) {
        this.invoiceRepository = invoiceRepository;
        this.fineRepository = fineRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
    }

    @Transactional
    public List<Invoice> generateInvoicesForReadyFines() {
        List<Fine> readyFines = fineRepository.findByIsReadyForInvoiceTrueAndIsPaidFalseAndInvoiceIsNull();
        List<Long> generatedInvoiceIds = new ArrayList<>();

        if (readyFines.isEmpty()) {
            return new ArrayList<>();
        }

        Map<User, List<Fine>> finesByUser = readyFines.stream()
                .collect(Collectors.groupingBy(fine -> fine.getLoan().getUser()));

        for (Map.Entry<User, List<Fine>> entry : finesByUser.entrySet()) {
            User user = entry.getKey();
            List<Fine> userFines = entry.getValue();

            double totalAmount = userFines.stream().mapToDouble(Fine::getFineAmount).sum();

            if (totalAmount > 0) {
                Invoice newInvoice = new Invoice();
                newInvoice.setInvoiceDate(LocalDate.now());
                newInvoice.setInvoicePeriod(LocalDate.now().getMonth().toString() + " " + LocalDate.now().getYear());
                newInvoice.setInvoiceAmount(totalAmount);
                newInvoice.setPaymentStatus(PaymentStatus.OPEN);
                newInvoice.setUser(user);
                newInvoice.setFines(userFines);

                Invoice savedInvoice = invoiceRepository.save(newInvoice);
                generatedInvoiceIds.add(savedInvoice.getInvoiceId());

                for (Fine fine : userFines) {
                    fine.setInvoice(savedInvoice);
                    fineRepository.save(fine);
                }
            }
        }

        return invoiceRepository.findAllById(generatedInvoiceIds);
    }

    @Transactional
    public Invoice processInvoicePayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new InvoiceAlreadyPaidException("This invoice has already been paid.");
        }

        invoice.setPaymentStatus(PaymentStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        List<Fine> finesOnInvoice = fineRepository.findByInvoice(invoice);
        for (Fine fine : finesOnInvoice) {
            fine.setIsPaid(true);
            fineRepository.save(fine);
        }

        return updatedInvoice;
    }

    public List<Invoice> getAllInvoices(){
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long invoiceId){
        return this.invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));
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
        return this.invoiceRepository.findByInvoicePeriodIgnoreCase(invoicePeriod);
    }

    public List<Invoice> getInvoicesByPaymentStatus(PaymentStatus paymentStatus){
        return this.invoiceRepository.findByPaymentStatus(paymentStatus);
    }

    public List<Invoice> getInvoicesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return invoiceRepository.findByUser(user);
    }

    public byte[] generateInvoicePdf(Long invoiceId) throws IOException {
        Invoice invoice = getInvoiceById(invoiceId);
        List<Fine> fines = fineRepository.findByInvoice(invoice);

        return pdfService.generatePdf(contentStream -> {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Factuur #" + invoice.getInvoiceId());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Factuurdatum: " + invoice.getInvoiceDate());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Periode: " + invoice.getInvoicePeriod());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Klant: " + invoice.getUser().getProfile().getFirstName() + " " + invoice.getUser().getProfile().getLastName());
            contentStream.endText();

            // Toevoegen van de boetes
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(50, 650);
            contentStream.showText("Boetes:");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 630);
            for (Fine fine : fines) {
                contentStream.showText("Boek: " + fine.getLoan().getBookCopy().getBook().getTitle() + " - bedrag: €" + String.format("%.2f", fine.getFineAmount()));
                contentStream.newLineAtOffset(0, -20);
            }
            contentStream.endText();

            // Toevoegen van het totaalbedrag
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 580);
            contentStream.showText("Totaal te betalen: €" + String.format("%.2f", invoice.getInvoiceAmount()));
            contentStream.endText();
        });
    }

    public boolean isInvoiceOwner(Long invoiceId, String username) {
        return invoiceRepository.existsByInvoiceIdAndUser_Username(invoiceId, username);
    }

    @Transactional
    public Invoice updateInvoice(Long invoiceId, InvoiceInputDto invoiceInputDto){
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        if (invoiceInputDto.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(invoiceInputDto.invoicePeriod);
        }

        if (invoiceInputDto.invoiceDate != null) {
            existingInvoice.setInvoiceDate(invoiceInputDto.invoiceDate);
        }

        if(invoiceInputDto.paymentStatus != null && !invoiceInputDto.paymentStatus.trim().isEmpty()){
            try {
                PaymentStatus newStatus = PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase());
                if (newStatus == PaymentStatus.PAID && existingInvoice.getPaymentStatus() != PaymentStatus.PAID) {
                    return processInvoicePayment(invoiceId);
                }
                existingInvoice.setPaymentStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment status: " + invoiceInputDto.paymentStatus, e);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    @Transactional
    public Invoice patchInvoice(Long invoiceId, InvoiceInputDto updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        PaymentStatus originalStatus = existingInvoice.getPaymentStatus();

        if (updates.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(updates.invoicePeriod);
        }
        if (updates.invoiceDate != null) {
            existingInvoice.setInvoiceDate(updates.invoiceDate);
        }

        if (updates.paymentStatus != null) {
            try {
                PaymentStatus newStatus = PaymentStatus.valueOf(updates.paymentStatus.trim().toUpperCase());
                existingInvoice.setPaymentStatus(newStatus);
                if (newStatus == PaymentStatus.PAID && originalStatus != PaymentStatus.PAID) {
                    return processInvoicePayment(invoiceId);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment status for patch: " + updates.paymentStatus, e);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    @Transactional
    public void deleteInvoice(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        this.invoiceRepository.deleteById(invoiceId);
    }
}