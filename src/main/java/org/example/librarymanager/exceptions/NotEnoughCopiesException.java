package org.example.librarymanager.exceptions;

public class NotEnoughCopiesException extends RuntimeException {
    public NotEnoughCopiesException(String message) {
        super(message);
    }
}
