package com.example.productapi.repository;

import com.example.productapi.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Stockage en mémoire (pas de base de données). */
@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public synchronized Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public synchronized Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public synchronized List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public synchronized void deleteById(Long id) {
        products.remove(id);
    }
}
