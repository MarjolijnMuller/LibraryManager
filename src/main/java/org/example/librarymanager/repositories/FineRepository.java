package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Fine;
import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.Loan;
import org.example.librarymanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByLoan(Loan loan);
    List<Fine> findByInvoice(Invoice invoice);
    List<Fine> findByIsPaidFalse();
}
