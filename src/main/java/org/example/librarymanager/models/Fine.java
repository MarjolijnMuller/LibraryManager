package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name="fines")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fineId;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double amount;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer overdueDays;

    @OneToOne
    @JoinColumn(name="loanId")
    @NotNull
    private Loan loan;

    @ManyToOne
    @JoinColumn(name="invoiceId")
    private Invoice invoice;

    public Fine(Double amount, Integer overdueDays, Loan loan, Invoice invoice) {
        this.amount = amount;
        this.overdueDays = overdueDays;
        this.loan = loan;
        this.invoice = invoice;
    }

    public Fine() {}

    public long getFineId() {
        return fineId;
    }

    public @NotNull @PositiveOrZero Double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @PositiveOrZero Double amount) {
        this.amount = amount;
    }

    public @NotNull @PositiveOrZero Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(@NotNull @PositiveOrZero Integer overdueDays) {
        this.overdueDays = overdueDays;
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
