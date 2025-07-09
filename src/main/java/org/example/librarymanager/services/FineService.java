package org.example.librarymanager.services;

import org.example.librarymanager.dtos.FineDto;
import org.example.librarymanager.dtos.FineInputDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.FineMapper;
import org.example.librarymanager.mappers.LoanMapper;
import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.repositories.FineRepository;
import org.example.librarymanager.repositories.InvoiceRepository;
import org.example.librarymanager.repositories.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FineService {
    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;
    private final InvoiceRepository invoiceRepository;

    public FineService(FineRepository fineRepository, LoanRepository loanRepository, InvoiceRepository invoiceRepository) {
        this.fineRepository = fineRepository;
        this.loanRepository = loanRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public FineDto createFine(FineInputDto fineInputDto) {
        Loan loan = loanRepository.findById(fineInputDto.loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + fineInputDto.loanId));

        Invoice invoice = invoiceRepository.findById(fineInputDto.invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + fineInputDto.invoiceId));

        Fine newFine = FineMapper.toEntity(fineInputDto, invoice, loan);
        Fine savedFine = fineRepository.save(newFine);
        return FineMapper.toResponseDto(savedFine);
    }

    public List<FineDto> getAllFines() {
        return FineMapper.toResponseDtoList(fineRepository.findAll());
    }

    public FineDto getFineById(Long fineId) {
        return FineMapper.toResponseDto(fineRepository.findById(fineId).orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + fineId)));
    }

    public List<FineDto> getFinesByLoanId(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        return FineMapper.toResponseDtoList(fineRepository.findByLoan(loan));
    }

    public List<FineDto> getFinesByInvoiceId(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));
        return FineMapper.toResponseDtoList(fineRepository.findByInvoice(invoice));
    }

    public List<FineDto> getFinesByNotPaid(Boolean isPaid) {
        return FineMapper.toResponseDtoList(fineRepository.findByIsPaidFalse());
    }

    public FineDto paidFine(Long fineId){
        Fine fine = fineRepository.findById(fineId). orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + fineId));

        if (fine.isPaid()) {
            throw new IllegalArgumentException("Fine already paid");
        }

        fine.setPaid(true);
        return FineMapper.toResponseDto(fineRepository.save(fine));
    }

    public FineDto updateFine(Long fineId, FineInputDto fineInputDto) {
        Fine existingFine = fineRepository.findById(fineId).orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + fineId));

        Loan loan = loanRepository.findById(fineInputDto.loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + fineInputDto.loanId));

        Invoice invoice = invoiceRepository.findById(fineInputDto.invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + fineInputDto.invoiceId));

        existingFine.setFineAmount(fineInputDto.fineAmount);
        existingFine.setFineDate(fineInputDto.fineDate);
        existingFine.setPaid(fineInputDto.isPaid);
        existingFine.setLoan(loan);
        existingFine.setInvoice(invoice);

        return FineMapper.toResponseDto(fineRepository.save(existingFine));
    }

    public FineDto patchFine(Long fineId, FineDto fineDto) {
        Fine existingFine = fineRepository.findById(fineId).orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + fineId));

        if (fineDto.fineAmount != null) {
            existingFine.setFineAmount(fineDto.fineAmount);
        }
        if (fineDto.fineDate != null) {
            existingFine.setFineDate(fineDto.fineDate);
        }
        if (fineDto.isPaid != existingFine.isPaid()) {
            existingFine.setPaid(fineDto.isPaid);
        }
        if (fineDto.loanId != null) {
            Loan loan = loanRepository.findById(fineDto.loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + fineDto.loanId));
            existingFine.setLoan(loan);
        }
        if (fineDto.invoiceId != null) {
            Invoice invoice = invoiceRepository.findById(fineDto.invoiceId).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + fineDto.invoiceId));
        }
        return FineMapper.toResponseDto(fineRepository.save(existingFine));
    }

    public void deleteFine(Long fineId) {
        this.fineRepository.deleteById(fineId);
    }
}
