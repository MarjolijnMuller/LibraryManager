package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="fines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double fineAmount;

    @NotNull
    @Column(nullable = false)
    private LocalDate fineDate;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @Column(nullable = false)
    private Boolean isReadyForInvoice = false;

    @ManyToOne
    @JoinColumn(name="loan_id")
    @NotNull
    private Loan loan;

    @ManyToOne
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    public Fine(Double fineAmount, LocalDate fineDate, Boolean isPaid, Loan loan, Invoice invoice, Boolean isReadyForInvoice) {
        this.fineAmount = fineAmount;
        this.fineDate = fineDate;
        this.isPaid = isPaid;
        this.loan = loan;
        this.invoice = invoice;
        this.isReadyForInvoice = false;
    }

    public Fine(Double fineAmount, LocalDate fineDate, Boolean isPaid, Loan loan, Boolean isReadyForInvoice, Invoice invoice) {
        this.fineAmount = fineAmount;
        this.fineDate = fineDate;
        this.isPaid = isPaid;
        this.loan = loan;
        this.invoice = null;
        this.isReadyForInvoice = false;
    }
}
