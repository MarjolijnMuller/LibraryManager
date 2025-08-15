package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Invoice;
import org.example.librarymanager.models.PaymentStatus;
import org.example.librarymanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByInvoiceDate(LocalDate invoiceDate);
    List<Invoice> findByInvoiceDateBeforeOrderByInvoiceDateAsc(LocalDate invoiceDate);
    List<Invoice> findByInvoiceDateAfterOrderByInvoiceDateDesc(LocalDate invoiceDate);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN ?1 AND ?2 ORDER BY i.invoiceDate ASC")
    List<Invoice> findInvoicesByDateRangeSorted(LocalDate startDate, LocalDate endDate);

    List<Invoice> findByInvoicePeriodIgnoreCase(String invoicePeriod);
    List<Invoice> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Invoice> findByUser(User user);
}