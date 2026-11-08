package com.example.productapi.dto;

import java.math.BigDecimal;

/** Représentation d'un produit renvoyée par l'API (record Java, exercice 3 du lab). */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity
) {
}
