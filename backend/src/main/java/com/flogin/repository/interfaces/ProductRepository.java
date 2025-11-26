package com.flogin.repository.interfaces;

import com.flogin.entity.Category;
import com.flogin.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @see Product
 * @see Category
 * @see com.flogin.service.ProductService
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    
    List<Product> findByCategory(Category category);
    
    /**
     * Tìm products theo product name (chứa keyword)
     */
    List<Product> findByProductNameContaining(String keyword);
    
    /**
     * Tìm products có giá trong khoảng
     */
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

     // Thêm method kiểm tra tên sản phẩm đã tồn tại
    boolean existsByProductName(String productName);

     //Thêm method kiểm tra tên trùng nhưng không phải chính sản phẩm đó (cho update)
    boolean existsByProductNameAndIdNot(String productName, Long id);
}
