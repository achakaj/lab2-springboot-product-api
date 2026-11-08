package com.example.productapi.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Produit introuvable avec l'id: " + id);
    }
}
