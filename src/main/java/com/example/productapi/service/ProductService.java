package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.exception.InvalidQuantityException;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private static final int MAX_QUANTITY = 1000;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        checkQuantity(request);
        Product product = new Product(null, request.name(), request.description(),
                request.price(), request.quantity());
        return mapToResponse(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ProductResponse getProductById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        checkQuantity(request);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        findOrThrow(id);
        productRepository.deleteById(id);
    }

    /** Exercice 1 : recherche par nom (contient, insensible à la casse). */
    public List<ProductResponse> searchByName(String name) {
        String needle = name == null ? "" : name.toLowerCase(Locale.ROOT);
        return productRepository.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase(Locale.ROOT).contains(needle))
                .map(this::mapToResponse)
                .toList();
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /** Exercice 2 : refuse les quantités supérieures à 1000. */
    private void checkQuantity(ProductRequest request) {
        if (request.quantity() != null && request.quantity() > MAX_QUANTITY) {
            throw new InvalidQuantityException("La quantité ne peut pas dépasser " + MAX_QUANTITY);
        }
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getQuantity());
    }
}
