package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Entity
@Table(name="fines")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fineId;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double fineAmount;

    @NotNull
    @Column(nullable = false)
    private LocalDate fineDate;

    @Column(nullable = false)
    private boolean isPaid = false;

    @OneToOne
    @JoinColumn(name="loan_id")
    @NotNull
    private Loan loan;

    @ManyToOne
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    public Fine(long fineId, Double fineAmount, LocalDate fineDate, boolean isPaid, Loan loan, Invoice invoice) {
        this.fineId = fineId;
        this.fineAmount = fineAmount;
        this.fineDate = fineDate;
        this.isPaid = isPaid;
        this.loan = loan;
        this.invoice = invoice;
    }

    public Fine() {}

    public long getFineId() {
        return fineId;
    }

    public void setFineId(long fineId) {
        this.fineId = fineId;
    }

    public @NotNull @PositiveOrZero Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(@NotNull @PositiveOrZero Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public @NotNull LocalDate getFineDate() {
        return fineDate;
    }

    public void setFineDate(@NotNull LocalDate fineDate) {
        this.fineDate = fineDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public @NotNull Loan getLoan() {
        return loan;
    }

    public void setLoan(@NotNull Loan loan) {
        this.loan = loan;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
