package com.flogin.controller;

import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    
   
    @Autowired
    private ProductService productService;


    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
            ProductDto createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

 
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Tạo Pageable object từ page và size parameters
        Pageable pageable = PageRequest.of(page, size);
        // Gọi service để lấy products với pagination
        Page<ProductDto> products = productService.getAll(pageable);
        return ResponseEntity.ok(products);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {
            // Gọi service để tìm product theo ID
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);

    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id, 
            @Valid @RequestBody UpdateProductRequest request) {
            // Gọi service để update product với thông tin mới
            ProductDto updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);

    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
            // Gọi service để xóa product khỏi database
            productService.deleteProduct(id);
            // Trả về 204 No Content để indicate successful deletion
            return ResponseEntity.noContent().build();

    }
}
