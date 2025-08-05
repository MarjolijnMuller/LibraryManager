package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

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

    @OneToMany(mappedBy="invoice", cascade = CascadeType.ALL)
    private List<Fine> fines = new ArrayList<>();

    //TODO: nullable false? alleen te maken met facturen van members
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
