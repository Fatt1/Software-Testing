package com.flogin.dto.ProductDtos;

import jakarta.validation.constraints.*;

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
