package com.flogin.repository.interfaces;

import com.flogin.entity.Category;
import com.flogin.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Các methods dưới đây được JpaRepository cung cấp sẵn:
     * - Optional<Product> findById(Long id)
     * - Page<Product> findAll(Pageable pageable)
     * - List<Product> findAll()
     * - Product save(Product product)
     * - void deleteById(Long id)
     * - void delete(Product product)
     * - boolean existsById(Long id)
     * - long count()
     */
    
    // Custom query methods theo naming convention của Spring Data JPA
    
    /**
     * Tìm products theo category
     */
    List<Product> findByCategory(Category category);
    
    /**
     * Tìm products theo product name (chứa keyword)
     */
    List<Product> findByProductNameContaining(String keyword);
    
    /**
     * Tìm products có giá trong khoảng
     */
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
}
