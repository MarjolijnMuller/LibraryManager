package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.example.librarymanager.dtos.LoanPatchDto;
import org.example.librarymanager.services.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody LoanInputDto loanInputDto) {
        LoanDto createdLoanDto = loanService.createLoan(loanInputDto);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + createdLoanDto.loanId)
                        .toUriString());
        return ResponseEntity.created(uri).body(createdLoanDto);
    }

    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        List<LoanDto> loans = loanService.getAllLoans();
        if (loans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Long loanId) {
        LoanDto loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDto>> getLoansByUser(@PathVariable Long userId) {
        List<LoanDto> loans = loanService.getLoansByUserId(userId);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/bookcopy/{bookCopyId}")
    public ResponseEntity<List<LoanDto>> getLoansByBookCopy(@PathVariable Long bookCopyId) {
        List<LoanDto> loans = loanService.getLoansByBookCopyId(bookCopyId);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanDto>> getOverdueLoans() {
        List<LoanDto> loans = loanService.getOverdueLoans();
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/outstanding")
    public ResponseEntity<List<LoanDto>> getOutstandingLoans() {
        List<LoanDto> loans = loanService.getOutstandingLoans();
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }


    @PutMapping("/{loanId}")
    public ResponseEntity<LoanDto> updateLoan(@Valid @PathVariable Long loanId, @Valid @RequestBody LoanInputDto loanInputDto) {
        LoanDto updatedLoan = loanService.updateLoan(loanId, loanInputDto);
        return ResponseEntity.ok(updatedLoan);
    }

    @PatchMapping("/{loanId}/return")
    public ResponseEntity<LoanDto> returnBookCopy(@PathVariable Long loanId) {
        LoanDto updatedLoan = loanService.returnBookCopy(loanId);
        return ResponseEntity.ok(updatedLoan);
    }

    @PatchMapping("/{loanId}")
    public ResponseEntity<LoanDto> patchLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody LoanPatchDto loanPatchDto) {
        LoanDto updatedLoan = loanService.patchLoan(loanId, loanPatchDto);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<LoanDto> deleteLoan(@PathVariable Long loanId) {
        this.loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }
}