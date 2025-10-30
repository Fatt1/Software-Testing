package com.flogin.repository.interfaces;

import com.flogin.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(long id);
    Page<Product> findAll(Pageable pageable);
    void deleteProduct(Product product);
    Product save(Product product);

    void deleteById(Product existingProduct);
}
