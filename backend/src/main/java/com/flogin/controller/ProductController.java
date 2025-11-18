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

/**
 * ProductController - REST API Controller cho Product management
 * Xử lý các CRUD operations cho Product
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    /**
     * POST /api/products - Tạo product mới
     * @param request CreateProductRequest chứa thông tin product
     * @return ProductDto đã được tạo với status 201 Created
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
            ProductDto createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

    /**
     * GET /api/products - Lấy danh sách tất cả products với pagination
     * @param page Số trang (mặc định = 0)
     * @param size Kích thước trang (mặc định = 10)
     * @return Page<ProductDto> với status 200 OK
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getAll(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Lấy thông tin một product theo ID
     * @param id ID của product
     * @return ProductDto với status 200 OK, hoặc 404 Not Found nếu không tìm thấy
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {

            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);

    }

    /**
     * PUT /api/products/{id} - Cập nhật product
     * @param id ID của product cần cập nhật
     * @param request UpdateProductRequest chứa thông tin mới
     * @return ProductDto đã được cập nhật với status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id, 
            @Valid @RequestBody UpdateProductRequest request) {
            ProductDto updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);

    }

    /**
     * DELETE /api/products/{id} - Xóa product
     * @param id ID của product cần xóa
     * @return Status 204 No Content nếu thành công, hoặc 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
       
    }
}
