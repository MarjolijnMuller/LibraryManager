package org.example.librarymanager.services;

import org.example.librarymanager.dtos.InvoiceInputDto;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final FineRepository fineRepository;
    private final UserRepository userRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, FineRepository fineRepository, UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.fineRepository = fineRepository;
        this.userRepository = userRepository;
    }


    public Invoice createInvoice(InvoiceInputDto invoiceInputDto){
        return this.invoiceRepository.save(InvoiceMapper.toEntity(invoiceInputDto));
    }

    public void generateInvoicesForReadyFines() {
        List<Fine> readyFines = fineRepository.findByIsReadyForInvoiceTrueAndIsPaidFalseAndInvoiceIsNull();

        if (readyFines.isEmpty()) {
            System.out.println("Geen boetes klaar voor facturatie.");
            return;
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

                Invoice savedInvoice = invoiceRepository.save(newInvoice);

                for (Fine fine : userFines) {
                    fine.setInvoice(savedInvoice);
                    fineRepository.save(fine);
                }
                System.out.println("Factuur gegenereerd voor gebruiker: " + user.getUserId() + " met bedrag: " + totalAmount);
            }
        }
    }

    public Invoice processInvoicePayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Factuur met ID " + invoiceId + " is al betaald.");
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

    public Invoice updateInvoice(Long invoiceId, InvoiceInputDto invoiceInputDto){
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        if (invoiceInputDto.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(invoiceInputDto.invoicePeriod);
        }

        existingInvoice.setInvoiceDate(invoiceInputDto.invoiceDate);
        existingInvoice.setInvoiceAmount(invoiceInputDto.invoiceAmount);

        if(invoiceInputDto.paymentStatus != null && !invoiceInputDto.paymentStatus.trim().isEmpty()){
            try {
                PaymentStatus newStatus = PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase());
                existingInvoice.setPaymentStatus(newStatus);
                if (newStatus == PaymentStatus.PAID) {
                    processInvoicePayment(invoiceId);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ongeldige betalingsstatus " + invoiceInputDto.paymentStatus);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    public Invoice patchInvoice(Long invoiceId, InvoiceInputDto updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        PaymentStatus originalStatus = existingInvoice.getPaymentStatus();

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
                PaymentStatus newStatus = PaymentStatus.valueOf(updates.paymentStatus.trim().toUpperCase());
                existingInvoice.setPaymentStatus(newStatus);
                if (newStatus == PaymentStatus.PAID && originalStatus != PaymentStatus.PAID) {
                    return processInvoicePayment(invoiceId);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ongeldige status van betaling voor patch: " + updates.paymentStatus, e);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    public void deleteInvoice(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

        this.invoiceRepository.deleteById(invoiceId);
    }
}
