package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.FineDto;
import org.example.librarymanager.dtos.FineInputDto;
import org.example.librarymanager.dtos.FinePatchDto;
import org.example.librarymanager.services.FineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/fines")
public class FineController {

    private final FineService fineService;
    public FineController(FineService fineService) {
        this.fineService = fineService;
    }


    @GetMapping
    public ResponseEntity<List<FineDto>> getAllFines() {
        List<FineDto> fines = fineService.getAllFines();
        return ResponseEntity.ok(fines);
    }

    @GetMapping("/{fineId}")
    public ResponseEntity<FineDto> getFineById(@PathVariable Long fineId) {
        FineDto fine = fineService.getFineById(fineId);
        return ResponseEntity.ok(fine);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<FineDto>> getFineByLoanId(@PathVariable Long loanId) {
        List<FineDto> fines = fineService.getFinesByLoanId(loanId);
        if (fines.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fines);
    }

    @GetMapping("/notPaid")
    public ResponseEntity<List<FineDto>> getNotPaidFines() {
        List<FineDto> fines = fineService.getFinesByNotPaid(false);
        if (fines.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fines);
    }

    @PutMapping("/{fineId}")
    public ResponseEntity<FineDto> updateFine(@Valid @PathVariable Long fineId, @Valid @RequestBody FineInputDto fineInputDto) {
        FineDto fine = fineService.updateFine(fineId, fineInputDto);
        return ResponseEntity.ok(fine);
    }

    @PatchMapping("/{fineId}/pay")
    public ResponseEntity<FineDto> payFine(@Valid @PathVariable Long fineId) {
        FineDto updatedFine = fineService.paidFine(fineId);
        return ResponseEntity.ok(updatedFine);
    }

    @PatchMapping("/{fineId}")
    public ResponseEntity<FineDto> patchFine(@PathVariable Long fineId, @Valid @RequestBody FinePatchDto finePatchDto) {
        FineDto updatedFine = fineService.patchFine(fineId, finePatchDto);
        return ResponseEntity.ok(updatedFine);
    }

    @DeleteMapping("/{fineId}")
    public ResponseEntity<FineDto> deleteFine(@PathVariable Long fineId) {
        fineService.deleteFine(fineId);
        return ResponseEntity.noContent().build();
    }
}
