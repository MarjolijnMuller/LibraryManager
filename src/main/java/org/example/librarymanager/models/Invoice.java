package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long InvoiceID;

    @NotNull
    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Size(min=3, max=250)
    private String invoicePeriod;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double invoiceAmount;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy="invoice")
    private List<Fine> fines = new ArrayList<>();

    public Invoice(LocalDate invoiceDate, String invoicePeriod, Double invoiceAmount, PaymentStatus paymentStatus, List<Fine> fines) {
        this.invoiceDate = invoiceDate;
        this.invoicePeriod = invoicePeriod;
        this.invoiceAmount = invoiceAmount;
        this.paymentStatus = paymentStatus;
        this.fines = fines;
    }

    public Invoice() {}

    public Long getInvoiceID() {
        return InvoiceID;
    }

    public @NotNull LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(@NotNull LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoicePeriod() {
        return invoicePeriod;
    }

    public void setInvoicePeriod(String invoicePeriod) {
        this.invoicePeriod = invoicePeriod;
    }

    public @NotNull @PositiveOrZero Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(@NotNull @PositiveOrZero Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public @NotNull PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(@NotNull PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<Fine> getFines() {
        return fines;
    }

    //TODO: opletten dat de fines bij elkaar komen
    public void setFines(List<Fine> fines) {
        this.fines = fines;
    }
    //TODO: factuur verwijderen
}
