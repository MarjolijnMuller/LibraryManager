package org.example.librarymanager.exceptions;

public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException(String message) {
        super(message);
    }
}
