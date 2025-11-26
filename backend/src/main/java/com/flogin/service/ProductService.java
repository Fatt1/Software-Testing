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
    
    /**
     * ProductRepository để access product data từ database
     */
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Jakarta Bean Validator để validate request DTOs
     */
    @Autowired
    private Validator validator;
    

    public ProductService(ProductRepository productRepository, Validator validator) {
        this.productRepository = productRepository;
        this.validator = validator;
    }
    
    /**
     * Default constructor cho Spring dependency injection
     */
    public ProductService() {
    }


    public ProductDto createProduct(CreateProductRequest request) {
        // Step 1: Validate DTO bằng Bean Validation annotations
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errorMessage = "Validation failed: " + violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(errorMessage);
        }
        
        // Step 2: Kiểm tra tên sản phẩm đã tồn tại chưa (business rule: unique name)
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product name '" + request.getProductName() + "' đã tồn tại");
        }
        
        // Step 3: Validate category với enum (phải là một trong các giá trị hợp lệ)
        if (!Category.isValid(request.getCategory())) {
            throw new IllegalArgumentException("Category '" + request.getCategory() + 
                    "' không hợp lệ. Các giá trị hợp lệ: " + Category.getAllValidValues());
        }

        // Convert category string sang enum
        Category category = Category.fromString(request.getCategory());

        Product product = new Product();
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        
        // Step 5: Lưu vào database
        Product savedProduct = productRepository.save(product);

        // Step 6: Convert Entity sang DTO và return
        return toDto(savedProduct);
    }


    public Page<ProductDto> getAll(Pageable pageable) {
        // Lấy products từ database với pagination
        Page<Product> productPage = productRepository.findAll(pageable);
        // Convert mỗi Product entity sang ProductDto
        return productPage.map(this::toDto);
    }


    public void deleteProduct(long id) {
        // Kiểm tra product có tồn tại không trước khi xóa
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        
        // Xóa product khỏi database
        productRepository.deleteById(id);
    }


    public ProductDto getProductById(long id) {
        // Tìm product trong database, throw exception nếu không tìm thấy
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        // Convert Entity sang DTO và return
        return toDto(product);
    }


    public ProductDto updateProduct(long id, UpdateProductRequest request) {


        // Step 1: Validate DTO bằng Bean Validation annotations
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errorMessage = "Validation failed: " + violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(errorMessage);
        }

        // Step 2: Tìm existing product, throw exception nếu không tồn tại
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));


        // Step 3: Kiểm tra tên sản phẩm đã tồn tại chưa
        // (Cần cải thiện: nên allow keep same name nếu update product đó)
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product name '" + request.getProductName() + "' đã tồn tại");
        }
        
        // Step 4: Validate category với enum values
        if (!Category.isValid(request.getCategory())) {
            throw new IllegalArgumentException("Category '" + request.getCategory() + 
                    "' không hợp lệ. Các giá trị hợp lệ: " + Category.getAllValidValues());
        }



        // Step 5: Cập nhật các fields của existing product
        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());
        existingProduct.setCategory(Category.fromString(request.getCategory()));
        existingProduct.setDescription(request.getDescription());

        // Step 6: Save updated product và return DTO
        Product updatedProduct = productRepository.save(existingProduct);
        return toDto(updatedProduct);
    }


    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getCategory().getValue(), // Convert enum to string
                product.getPrice(),
                product.getProductName(),
                product.getDescription(),
                product.getQuantity()
        );
    }
}