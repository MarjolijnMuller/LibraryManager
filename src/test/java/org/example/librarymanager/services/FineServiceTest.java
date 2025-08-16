package org.example.librarymanager.services;

import org.example.librarymanager.dtos.FineDto;
import org.example.librarymanager.dtos.FineInputDto;
import org.example.librarymanager.dtos.FinePatchDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.repositories.FineRepository;
import org.example.librarymanager.repositories.InvoiceRepository;
import org.example.librarymanager.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FineServiceTest {

    @Mock
    private FineRepository fineRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private FineService fineService;

    private Fine createFine(Long fineId, Double fineAmount, Boolean isPaid, Loan loan, Invoice invoice) {
        Fine fine = new Fine();
        fine.setFineId(fineId);
        fine.setFineAmount(fineAmount);
        fine.setFineDate(LocalDate.now());
        fine.setIsPaid(isPaid);
        fine.setLoan(loan);
        fine.setInvoice(invoice);
        return fine;
    }

    private Loan createLoan(Long loanId) {
        Loan loan = new Loan();
        loan.setLoanId(loanId);
        return loan;
    }

    private Invoice createInvoice(Long invoiceId) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(invoiceId);
        return invoice;
    }

    @Test
    void getAllFines_ReturnsAllFines() {
        Fine fine1 = createFine(1L, 10.0, false, null, null);
        when(fineRepository.findAll()).thenReturn(Collections.singletonList(fine1));

        List<FineDto> result = fineService.getAllFines();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fineRepository).findAll();
    }

    @Test
    void getFineById_FineFound_ReturnsFine() {
        Fine fine1 = createFine(1L, 10.0, false, null, null);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine1));

        FineDto result = fineService.getFineById(1L);

        assertNotNull(result);
        assertEquals(1L, result.fineId);
        verify(fineRepository).findById(1L);
    }

    @Test
    void getFineById_FineNotFound_ThrowsException() {
        when(fineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.getFineById(1L));
        verify(fineRepository).findById(1L);
    }

    @Test
    void getFinesByLoanId_LoanFound_ReturnsFines() {
        Loan loan1 = createLoan(1L);
        Fine fine1 = createFine(1L, 10.0, false, loan1, null);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan1));
        when(fineRepository.findByLoan(loan1)).thenReturn(Collections.singletonList(fine1));

        List<FineDto> result = fineService.getFinesByLoanId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findById(1L);
        verify(fineRepository).findByLoan(loan1);
    }

    @Test
    void getFinesByLoanId_LoanNotFound_ThrowsException() {
        when(loanRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.getFinesByLoanId(1L));
        verify(loanRepository).findById(1L);
        verify(fineRepository, never()).findByLoan(any(Loan.class));
    }

    @Test
    void getFinesByInvoiceId_InvoiceFound_ReturnsFines() {
        Invoice invoice1 = createInvoice(1L);
        Fine fine1 = createFine(1L, 10.0, false, null, invoice1);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice1));
        when(fineRepository.findByInvoice(invoice1)).thenReturn(Collections.singletonList(fine1));

        List<FineDto> result = fineService.getFinesByInvoiceId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(invoiceRepository).findById(1L);
        verify(fineRepository).findByInvoice(invoice1);
    }

    @Test
    void getFinesByInvoiceId_InvoiceNotFound_ThrowsException() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.getFinesByInvoiceId(1L));
        verify(invoiceRepository).findById(1L);
        verify(fineRepository, never()).findByInvoice(any(Invoice.class));
    }

    @Test
    void getFinesByNotPaid_ReturnsUnpaidFines() {
        Fine fine1 = createFine(1L, 10.0, false, null, null);
        when(fineRepository.findByIsPaidFalse()).thenReturn(Collections.singletonList(fine1));

        List<FineDto> result = fineService.getFinesByNotPaid(false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fineRepository).findByIsPaidFalse();
    }

    @Test
    void paidFine_FineFoundAndNotPaid_UpdatesFine() {
        Fine fine1 = createFine(1L, 10.0, false, null, null);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine1));
        when(fineRepository.save(any(Fine.class))).thenReturn(fine1);

        FineDto result = fineService.paidFine(1L);

        assertTrue(fine1.getIsPaid());
        assertEquals(fine1.getFineAmount(), result.fineAmount);
        verify(fineRepository).findById(1L);
        verify(fineRepository).save(fine1);
    }

    @Test
    void paidFine_FineNotFound_ThrowsException() {
        when(fineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.paidFine(1L));
        verify(fineRepository).findById(1L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void paidFine_FineAlreadyPaid_ThrowsException() {
        Fine fine1 = createFine(1L, 10.0, true, null, null);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine1));

        assertThrows(IllegalStateException.class, () -> fineService.paidFine(1L));
        verify(fineRepository).findById(1L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void updateFine_AllFieldsUpdated_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), createInvoice(1L));
        FineInputDto updateDto = new FineInputDto();
        updateDto.loanId = 2L;
        updateDto.fineAmount = 20.0;
        updateDto.fineDate = LocalDate.of(2023, 1, 1);
        updateDto.isPaid = true;
        updateDto.invoiceId = 2L;

        Loan newLoan = createLoan(2L);
        Invoice newInvoice = createInvoice(2L);

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(loanRepository.findById(2L)).thenReturn(Optional.of(newLoan));
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(newInvoice));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.updateFine(1L, updateDto);

        assertEquals(2L, result.loanId);
        assertEquals(20.0, result.fineAmount);
        assertEquals(LocalDate.of(2023, 1, 1), result.fineDate);
        assertTrue(result.isPaid);
        assertEquals(2L, result.invoiceId);
        verify(fineRepository).findById(1L);
        verify(loanRepository).findById(2L);
        verify(invoiceRepository).findById(2L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void updateFine_UpdatesLoanOnly_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FineInputDto updateDto = new FineInputDto();
        updateDto.loanId = 2L;
        updateDto.fineAmount = 10.0;
        updateDto.fineDate = LocalDate.now();
        updateDto.isPaid = false;

        Loan newLoan = createLoan(2L);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(loanRepository.findById(2L)).thenReturn(Optional.of(newLoan));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.updateFine(1L, updateDto);

        assertEquals(2L, result.loanId);
        verify(fineRepository).findById(1L);
        verify(loanRepository).findById(2L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void updateFine_UpdatesInvoiceOnly_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FineInputDto updateDto = new FineInputDto();
        updateDto.invoiceId = 2L;
        updateDto.fineAmount = 10.0;
        updateDto.fineDate = LocalDate.now();
        updateDto.isPaid = false;

        Invoice newInvoice = createInvoice(2L);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(newInvoice));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.updateFine(1L, updateDto);

        assertEquals(2L, result.invoiceId);
        verify(fineRepository).findById(1L);
        verify(invoiceRepository).findById(2L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void updateFine_UpdateInvoiceToNull_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), createInvoice(1L));
        FineInputDto updateDto = new FineInputDto();
        updateDto.invoiceId = null;
        updateDto.fineAmount = 10.0;
        updateDto.fineDate = LocalDate.now();
        updateDto.isPaid = false;

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.updateFine(1L, updateDto);

        assertNull(result.invoiceId);
        verify(fineRepository).findById(1L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void updateFine_FineNotFound_ThrowsException() {
        FineInputDto updateDto = new FineInputDto();
        when(fineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.updateFine(1L, updateDto));
        verify(fineRepository).findById(1L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void updateFine_NewLoanNotFound_ThrowsException() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FineInputDto updateDto = new FineInputDto();
        updateDto.loanId = 99L;
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.updateFine(1L, updateDto));
        verify(fineRepository).findById(1L);
        verify(loanRepository).findById(99L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void updateFine_NewInvoiceNotFound_ThrowsException() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FineInputDto updateDto = new FineInputDto();
        updateDto.loanId = 1L;
        updateDto.invoiceId = 99L;
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.updateFine(1L, updateDto));
        verify(fineRepository).findById(1L);
        verify(invoiceRepository).findById(99L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void patchFine_PartialFieldsUpdated_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), createInvoice(1L));
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.fineAmount = 25.0;
        patchDto.isPaid = true;

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.patchFine(1L, patchDto);

        assertEquals(25.0, result.fineAmount);
        assertTrue(result.isPaid);
        verify(fineRepository).findById(1L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void patchFine_UpdatesLoanOnly_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.loanId = 2L;

        Loan newLoan = createLoan(2L);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(loanRepository.findById(2L)).thenReturn(Optional.of(newLoan));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.patchFine(1L, patchDto);

        assertEquals(2L, result.loanId);
        verify(fineRepository).findById(1L);
        verify(loanRepository).findById(2L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void patchFine_UpdatesInvoiceOnly_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.invoiceId = 2L;

        Invoice newInvoice = createInvoice(2L);
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(newInvoice));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.patchFine(1L, patchDto);

        assertEquals(2L, result.invoiceId);
        verify(fineRepository).findById(1L);
        verify(invoiceRepository).findById(2L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void patchFine_UpdateInvoiceToNull_UpdatesFine() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), createInvoice(1L));
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.invoiceId = null;

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        FineDto result = fineService.patchFine(1L, patchDto);

        assertNull(result.invoiceId);
        verify(fineRepository).findById(1L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void patchFine_UpdateFineDateOnly_UpdatesFine() {
        // Arrange
        Fine existingFine = createFine(1L, 10.0, false, null, null);
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.fineDate = LocalDate.of(2024, 1, 1);

        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(any(Fine.class))).thenReturn(existingFine);

        // Act
        fineService.patchFine(1L, patchDto);

        // Assert
        assertEquals(LocalDate.of(2024, 1, 1), existingFine.getFineDate());
        verify(fineRepository).findById(1L);
        verify(fineRepository).save(existingFine);
    }

    @Test
    void patchFine_FineNotFound_ThrowsException() {
        FinePatchDto patchDto = new FinePatchDto();
        when(fineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.patchFine(1L, patchDto));
        verify(fineRepository).findById(1L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void patchFine_LoanNotFound_ThrowsException() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.loanId = 99L;
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.patchFine(1L, patchDto));
        verify(fineRepository).findById(1L);
        verify(loanRepository).findById(99L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void patchFine_InvoiceNotFound_ThrowsException() {
        Fine existingFine = createFine(1L, 10.0, false, createLoan(1L), null);
        FinePatchDto patchDto = new FinePatchDto();
        patchDto.invoiceId = 99L;
        when(fineRepository.findById(1L)).thenReturn(Optional.of(existingFine));
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fineService.patchFine(1L, patchDto));
        verify(fineRepository).findById(1L);
        verify(invoiceRepository).findById(99L);
        verify(fineRepository, never()).save(any(Fine.class));
    }

    @Test
    void patchFine_RemoveInvoice_SetsInvoiceToNull() {
        // Arrange
        Long fineId = 1L;
        Fine existingFine = new Fine();
        existingFine.setFineId(fineId);
        existingFine.setInvoice(new Invoice());

        FinePatchDto patchDto = new FinePatchDto();
        patchDto.invoiceId = null;

        when(fineRepository.findById(fineId)).thenReturn(Optional.of(existingFine));
        when(fineRepository.save(existingFine)).thenReturn(existingFine);

        // Act
        fineService.patchFine(fineId, patchDto);

        // Assert
        verify(fineRepository).findById(fineId);
        verify(fineRepository).save(existingFine);
        assertNull(existingFine.getInvoice());
    }

    @Test
    void deleteFine_FineFound_DeletesFine() {
        when(fineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(fineRepository).deleteById(1L);

        fineService.deleteFine(1L);

        verify(fineRepository).existsById(1L);
        verify(fineRepository).deleteById(1L);
    }

    @Test
    void deleteFine_FineNotFound_ThrowsException() {
        when(fineRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> fineService.deleteFine(1L));
        verify(fineRepository).existsById(1L);
        verify(fineRepository, never()).deleteById(anyLong());
    }

}