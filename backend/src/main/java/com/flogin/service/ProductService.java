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

/**
 * ProductService - Service Layer for Product Management Business Logic
 * 
 * <p>Service này xử lý toàn bộ business logic liên quan đến product management,
 * bao gồm CRUD operations, validation, và data transformation.</p>
 * 
 * <p>Core Responsibilities:</p>
 * <ul>
 *   <li>Create new products với full validation</li>
 *   <li>Retrieve products với pagination support</li>
 *   <li>Update existing products</li>
 *   <li>Delete products khỏi database</li>
 *   <li>Validate product data (name uniqueness, category validity, etc.)</li>
 *   <li>Convert between Entity và DTO objects</li>
 * </ul>
 * 
 * <p>Validation Rules:</p>
 * <ul>
 *   <li>Product name must be unique trong hệ thống</li>
 *   <li>Category must be valid enum value</li>
 *   <li>Price must be positive number</li>
 *   <li>Quantity must be non-negative integer</li>
 *   <li>All required fields must be provided</li>
 * </ul>
 * 
 * <p>Error Handling:</p>
 * <ul>
 *   <li>IllegalArgumentException - Validation errors hoặc duplicate names</li>
 *   <li>NoSuchElementException - Product not found errors</li>
 * </ul>
 * 
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 * @see Product
 * @see ProductDto
 * @see ProductRepository
 */
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
    
    /**
     * Constructor cho testing purposes
     * 
     * <p>Cho phép inject mock objects khi viết unit tests,
     * giúp test service logic mà không cần real database.</p>
     * 
     * @param productRepository Mock ProductRepository cho testing
     * @param validator Mock Validator cho testing
     */
    public ProductService(ProductRepository productRepository, Validator validator) {
        this.productRepository = productRepository;
        this.validator = validator;
    }
    
    /**
     * Default constructor cho Spring dependency injection
     */
    public ProductService() {
    }

    /**
     * Tạo product mới trong hệ thống
     * 
     * <p>Method này thực hiện full product creation flow:</p>
     * <ol>
     *   <li>Validate input data với Bean Validation constraints</li>
     *   <li>Kiểm tra product name uniqueness</li>
     *   <li>Validate category với enum values</li>
     *   <li>Create và save product entity</li>
     *   <li>Convert và return ProductDto</li>
     * </ol>
     * 
     * <p><b>Validation Checks:</b></p>
     * <ul>
     *   <li>All @NotBlank, @Positive, @Min constraints</li>
     *   <li>Product name must be unique (case-sensitive)</li>
     *   <li>Category must be valid enum value (Electronics, Clothing, Food, etc.)</li>
     *   <li>Price must be positive number</li>
     *   <li>Quantity must be >= 0</li>
     * </ul>
     * 
     * <p><b>Example Success Flow:</b></p>
     * <pre>
     * Input: CreateProductRequest {
     *   productName: "iPhone 15 Pro",
     *   category: "Electronics",
     *   price: 999.99,
     *   quantity: 50,
     *   description: "Latest model"
     * }
     * → Validate: OK
     * → Check Name: Unique
     * → Check Category: Valid
     * → Save to DB: Success (ID = 1)
     * Output: ProductDto { id: 1, ... }
     * </pre>
     * 
     * @param request CreateProductRequest chứa thông tin product cần tạo
     * @return ProductDto của product đã được tạo thành công
     * @throws IllegalArgumentException nếu:
     *         - Validation fails (missing fields, invalid format)
     *         - Product name đã tồn tại
     *         - Category không hợp lệ
     */
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

        // Step 4: Tạo Product entity với constructor mặc định và set fields
        // (ID sẽ được database tự sinh thông qua auto-increment)
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

    /**
     * Lấy danh sách tất cả products với pagination
     * 
     * <p>Retrieve products từ database với pagination support để optimize performance.
     * Trả về Page object chứa products và metadata (total pages, total elements, etc.)</p>
     * 
     * <p><b>Pagination Benefits:</b></p>
     * <ul>
     *   <li>Reduce memory usage khi có nhiều products</li>
     *   <li>Faster response time</li>
     *   <li>Better user experience với infinite scroll hoặc page navigation</li>
     * </ul>
     * 
     * @param pageable Pageable object chứa page number, page size, và sorting info
     * @return Page&lt;ProductDto&gt; chứa list of products và pagination metadata
     */
    public Page<ProductDto> getAll(Pageable pageable) {
        // Lấy products từ database với pagination
        Page<Product> productPage = productRepository.findAll(pageable);
        // Convert mỗi Product entity sang ProductDto
        return productPage.map(this::toDto);
    }

    /**
     * Xóa product khỏi database
     * 
     * <p>Tìm product theo ID và xóa khỏi database.
     * Operation này là permanent và không thể undo.</p>
     * 
     * <p><b>Note:</b> Nên kiểm tra product existence trước khi delete
     * để throw meaningful error message nếu product không tồn tại.</p>
     * 
     * @param id ID của product cần xóa
     * @throws NoSuchElementException nếu không tìm thấy product với ID đã cho
     */
    public void deleteProduct(long id) {
        // Kiểm tra product có tồn tại không trước khi xóa
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        
        // Xóa product khỏi database
        productRepository.deleteById(id);
    }

    /**
     * Lấy thông tin chi tiết của một product theo ID
     * 
     * <p>Tìm kiếm product trong database và trả về ProductDto.
     * Sử dụng cho product detail page hoặc edit form.</p>
     * 
     * @param id ID của product cần lấy
     * @return ProductDto chứa thông tin đầy đủ của product
     * @throws NoSuchElementException nếu không tìm thấy product với ID đã cho
     */
    public ProductDto getProductById(long id) {
        // Tìm product trong database, throw exception nếu không tìm thấy
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        // Convert Entity sang DTO và return
        return toDto(product);
    }

    /**
     * Cập nhật thông tin product existing
     * 
     * <p>Update product với validation đầy đủ tương tự create operation.
     * Kiểm tra product existence, validate input, check uniqueness và category validity.</p>
     * 
     * <p><b>Update Flow:</b></p>
     * <ol>
     *   <li>Validate input data với Bean Validation</li>
     *   <li>Tìm existing product trong database</li>
     *   <li>Check product name uniqueness (nếu name thay đổi)</li>
     *   <li>Validate category value</li>
     *   <li>Update các fields</li>
     *   <li>Save và return updated product</li>
     * </ol>
     * 
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>All Bean Validation constraints must pass</li>
     *   <li>Product name must be unique (nếu khác tên cũ)</li>
     *   <li>Category must be valid enum value</li>
     *   <li>Price và quantity must be valid numbers</li>
     * </ul>
     * 
     * @param id ID của product cần update
     * @param request UpdateProductRequest chứa thông tin mới
     * @return ProductDto của product đã được cập nhật
     * @throws IllegalArgumentException nếu:
     *         - Validation fails
     *         - Product name đã tồn tại (duplicate)
     *         - Category không hợp lệ
     * @throws NoSuchElementException nếu product với ID không tồn tại
     */
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



    /**
     * Helper method: Chuyển đổi từ Product Entity sang ProductDto
     * 
     * <p>DTO (Data Transfer Object) pattern để:</p>
     * <ul>
     *   <li>Tách biệt internal entity structure khỏi API response</li>
     *   <li>Control exactly những fields nào được expose ra external</li>
     *   <li>Avoid exposing sensitive data hoặc JPA proxy objects</li>
     *   <li>Allow different representations cho different use cases</li>
     * </ul>
     * 
     * <p><b>Mapping:</b></p>
     * <pre>
     * Product Entity → ProductDto:
     *   - id → id
     *   - category (Enum) → category (String)
     *   - price → price
     *   - productName → productName
     *   - description → description
     *   - quantity → quantity
     * </pre>
     * 
     * @param product Product entity từ database
     * @return ProductDto ready để send to client
     */
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