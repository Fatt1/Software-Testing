package com.flogin.service;

import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.entity.Category;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private Validator validator;
    
    // Constructor cho testing (để inject mock objects)
    public ProductService(ProductRepository productRepository, Validator validator) {
        this.productRepository = productRepository;
        this.validator = validator;
    }
    
    // Constructor mặc định cho Spring
    public ProductService() {
    }

    public ProductDto createProduct(CreateProductRequest request) {
        // Validate DTO bằng Bean Validation
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errorMessage = "Validation failed: " + violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(errorMessage);
        }
        
        // Kiểm tra tên sản phẩm đã tồn tại chưa
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product name '" + request.getProductName() + "' đã tồn tại");
        }
        
        // Validate category với enum
        if (!Category.isValid(request.getCategory())) {
            throw new IllegalArgumentException("Category '" + request.getCategory() + 
                    "' không hợp lệ. Các giá trị hợp lệ: " + Category.getAllValidValues());
        }

        // Convert category string sang enum
        Category category = Category.fromString(request.getCategory());

        // Tạo Product với constructor mặc định và set fields (ID sẽ được database tự sinh)
        Product product = new Product();
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        
        Product savedProduct = productRepository.save(product);

        return toDto(savedProduct);
    }

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::toDto);
    }

    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        
        productRepository.deleteById(id);
    }

    public ProductDto getProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return toDto(product);
    }

    public ProductDto updateProduct(long id, UpdateProductRequest request) {
        // Validate DTO bằng Bean Validation
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errorMessage = "Validation failed: " + violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(errorMessage);
        }

        // Kiểm tra tên sản phẩm đã tồn tại chưa
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product name '" + request.getProductName() + "' đã tồn tại");
        }
        
        // Validate category với enum
        if (!Category.isValid(request.getCategory())) {
            throw new IllegalArgumentException("Category '" + request.getCategory() + 
                    "' không hợp lệ. Các giá trị hợp lệ: " + Category.getAllValidValues());
        }

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        // Cập nhật các trường
        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());
        existingProduct.setCategory(Category.fromString(request.getCategory()));
        existingProduct.setDescription(request.getDescription());

        Product updatedProduct = productRepository.save(existingProduct);
        return toDto(updatedProduct);
    }



    /**
     * Chuyển đổi từ Product (Entity) sang ProductDto (DTO).
     */
    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getCategory().getValue(),
                product.getPrice(),
                product.getProductName(),
                product.getDescription(),
                product.getQuantity()
        );
    }
}