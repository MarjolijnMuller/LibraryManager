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
import org.springframework.transaction.annotation.Transactional;

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

    public InvoiceService(InvoiceRepository invoiceRepository, FineRepository fineRepository, UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.fineRepository = fineRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Invoice createInvoice(InvoiceInputDto invoiceInputDto){
        User user = null;
        if (invoiceInputDto.userId != null) {
            user = userRepository.findById(invoiceInputDto.userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gebruiker niet gevonden met ID: " + invoiceInputDto.userId));
        }

        Invoice newInvoice = InvoiceMapper.toEntity(invoiceInputDto);
        newInvoice.setUser(user);

        return this.invoiceRepository.save(newInvoice);
    }

    @Transactional
    public List<Invoice> generateInvoicesForReadyFines() {
        List<Fine> readyFines = fineRepository.findByIsReadyForInvoiceTrueAndIsPaidFalseAndInvoiceIsNull();
        List<Invoice> generatedInvoices = new ArrayList<>();

        if (readyFines.isEmpty()) {
            System.out.println("Geen boetes klaar voor facturatie op " + LocalDate.now() + ".");
            return generatedInvoices;
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
                generatedInvoices.add(savedInvoice);

                for (Fine fine : userFines) {
                    fine.setInvoice(savedInvoice);
                    fineRepository.save(fine);
                }
                System.out.println("Factuur gegenereerd voor gebruiker: " + user.getUserId() + " met bedrag: " + totalAmount + " op " + LocalDate.now() + ".");
            }
        }
        return generatedInvoices;
    }

    @Transactional
    public Invoice processInvoicePayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Factuur met ID " + invoiceId + " is al betaald.");
        }

        invoice.setPaymentStatus(PaymentStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // Markeer alle bijbehorende boetes als betaald
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
        return this.invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));
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

    @Transactional
    public Invoice updateInvoice(Long invoiceId, InvoiceInputDto invoiceInputDto){
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

        if (invoiceInputDto.invoicePeriod != null) {
            existingInvoice.setInvoicePeriod(invoiceInputDto.invoicePeriod);
        }

        if (invoiceInputDto.invoiceDate != null) {
            existingInvoice.setInvoiceDate(invoiceInputDto.invoiceDate);
        }
        if (invoiceInputDto.invoiceAmount != null) {
            existingInvoice.setInvoiceAmount(invoiceInputDto.invoiceAmount);
        }

        if(invoiceInputDto.paymentStatus != null && !invoiceInputDto.paymentStatus.trim().isEmpty()){
            try {
                PaymentStatus newStatus = PaymentStatus.valueOf(invoiceInputDto.paymentStatus.trim().toUpperCase());
                if (newStatus == PaymentStatus.PAID && existingInvoice.getPaymentStatus() != PaymentStatus.PAID) {
                    return processInvoicePayment(invoiceId);
                }
                existingInvoice.setPaymentStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ongeldige betalingsstatus: " + invoiceInputDto.paymentStatus, e);
            }
        }

        return this.invoiceRepository.save(existingInvoice);
    }

    @Transactional
    public Invoice patchInvoice(Long invoiceId, InvoiceInputDto updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

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

    @Transactional
    public void deleteInvoice(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Factuur niet gevonden met ID: " + invoiceId));

        this.invoiceRepository.deleteById(invoiceId);
    }
}