package com.example.productapi.exception;

/** Exercice 2 : levée lorsque la quantité dépasse 1000. */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String message) {
        super(message);
    }
}
