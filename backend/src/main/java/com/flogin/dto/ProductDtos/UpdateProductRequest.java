package com.flogin.dto.ProductDtos;

import jakarta.validation.constraints.*;

/**
 * UpdateProductRequest - Data Transfer Object for Product Updates
 * 
 * Purpose:
 * This DTO encapsulates the data required to update an existing product.
 * It serves as the request body for the PUT /api/products/{id} endpoint.
 * 
 * Design Pattern: Command Object / Update DTO
 * - Separates update request from domain model
 * - Enforces validation rules for updates
 * - Prevents partial updates with invalid data
 * - Documents updatable fields
 * 
 * Key Characteristics:
 * - No 'id' field (ID comes from path parameter: /api/products/{id})
 * - All fields are required (full update, not partial)
 * - Same validation rules as CreateProductRequest
 * - Used only for UPDATE operations
 * 
 * Difference from CreateProductRequest:
 * Functionally identical validation rules, but:
 * - Different semantic meaning (create vs update)
 * - Used in different endpoints (POST vs PUT)
 * - Allows for future divergence if update rules differ
 * - Better code clarity and API documentation
 * 
 * Validation Rules:
 * (Identical to CreateProductRequest for consistency)
 * 
 * productName: @NotBlank, @Size(min=3, max=100)
 * price: @NotNull, @DecimalMin("0.01"), @DecimalMax("999999999")
 * description: @Size(max=500) - optional field
 * quantity: @NotNull, @Min(0), @Max(99999)
 * category: @NotBlank - must be valid Category enum value
 * 
 * JSON Request Example (PUT /api/products/12345):
 * <pre>
 * {
 *   "productName": "Laptop Dell XPS 15 (Updated)",
 *   "price": 23000000.00,
 *   "description": "Giảm giá 20% - Laptop cao cấp",
 *   "quantity": 45,
 *   "category": "Điện tử"
 * }
 * </pre>
 * 
 * Success Response (200 OK):
 * <pre>
 * {
 *   "id": 12345,
 *   "productName": "Laptop Dell XPS 15 (Updated)",
 *   "price": 23000000.00,
 *   "description": "Giảm giá 20% - Laptop cao cấp",
 *   "quantity": 45,
 *   "category": "Điện tử"
 * }
 * </pre>
 * 
 * Error Responses:
 * 
 * 404 NOT FOUND - Product doesn't exist:
 * <pre>
 * {
 *   "message": "Product not found with id: 12345"
 * }
 * </pre>
 * 
 * 400 BAD REQUEST - Validation failed:
 * <pre>
 * {
 *   "message": "Validation failed",
 *   "errors": [
 *     "Price phải > 0",
 *     "Quantity phải >= 0"
 *   ]
 * }
 * </pre>
 * 
 * Usage in Controller:
 * <pre>
 * {@code
 * @PutMapping("/{id}")
 * public ResponseEntity<ProductDto> updateProduct(
 *         @PathVariable String id,
 *         @Valid @RequestBody UpdateProductRequest request) {
 *     ProductDto updatedProduct = productService.updateProduct(id, request);
 *     return ResponseEntity.ok(updatedProduct);
 * }
 * }
 * </pre>
 * 
 * Update Flow:
 * 1. Client sends PUT request with product ID and updated data
 * 2. @Valid annotation triggers validation
 * 3. Service finds existing product by ID
 * 4. Service updates product fields with request data
 * 5. Service saves updated product to database
 * 6. Service returns ProductDto with updated values
 * 
 * Partial Update Consideration:
 * This DTO requires all fields (full update). For partial updates (PATCH):
 * - Create a separate PartialUpdateProductRequest
 * - Make all fields Optional<T> or nullable
 * - Only update non-null fields
 * 
 * Full Update Example:
 * <pre>
 * {@code
 * // Must provide all fields
 * PUT /api/products/123
 * {
 *   "productName": "New Name",
 *   "price": 100.00,
 *   "description": "New desc",
 *   "quantity": 50,
 *   "category": "Điện tử"
 * }
 * }
 * </pre>
 * 
 * Business Rules:
 * - Product ID must exist (checked by service layer)
 * - All validation rules must pass
 * - Category change is allowed
 * - Price can be increased or decreased
 * - Quantity can be set to 0 (out of stock)
 * - Description can be cleared (empty string)
 * 
 * Concurrency:
 * - No optimistic locking implemented (last write wins)
 * - For high-concurrency scenarios, add version field
 * - Consider using @Version annotation in entity
 * 
 * @see ProductDto
 * @see CreateProductRequest
 * @see com.flogin.entity.Product
 */
/**
 * UpdateProductRequest - DTO cho việc cập nhật product
 * Không có field id vì id được lấy từ path parameter
 */
public class UpdateProductRequest {
    
    @NotBlank(message = "Product Name không được rỗng")
    @Size(min = 3, max = 100, message = "Product Name phải từ 3 đến 100 ký tự")
    private String productName;
    
    @NotNull(message = "Price không được để trống")
    @DecimalMin(value = "0.01", message = "Price phải > 0")
    @DecimalMax(value = "999999999", message = "Price không được vượt quá 999,999,999")
    private Double price;
    
    @Size(max = 500, message = "Description không được quá 500 ký tự")
    private String description;
    
    @NotNull(message = "Quantity không được để trống")
    @Min(value = 0, message = "Quantity phải >= 0")
    @Max(value = 99999, message = "Quantity không được vượt quá 99,999")
    private Integer quantity;
    
    @NotBlank(message = "Category không được rỗng")
    private String category;

    // Constructor mặc định
    public UpdateProductRequest() {
    }

    public UpdateProductRequest(String productName, Double price, String description, Integer quantity, String category) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
