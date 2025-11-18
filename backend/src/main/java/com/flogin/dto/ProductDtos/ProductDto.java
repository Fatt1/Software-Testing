package com.flogin.dto.ProductDtos;

import com.flogin.entity.Product;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductDto {

    private long id;
    
    @NotNull(message = "Price không được để trống")
    @DecimalMin(value = "0.01", message = "Price phải > 0")
    @DecimalMax(value = "999999999", message = "Price không được vượt quá 999,999,999")
    private Double price;
    
    @NotBlank(message = "Product Name không được rỗng")
    @Size(min = 3, max = 100, message = "Product Name phải từ 3 đến 100 ký tự")
    private String productName;
    
    @NotNull(message = "Quantity không được để trống")
    @Min(value = 0, message = "Quantity phải >= 0")
    @Max(value = 99999, message = "Quantity không được vượt quá 99,999")
    private Integer quantity;
    
    @Size(max = 500, message = "Description không được quá 500 ký tự")
    private String description;
    
    @NotBlank(message = "Category không được rỗng")
    private String category;


    public ProductDto(long id, String category, Double price, String productName, String description, Integer quantity) {
        this.id = id;
        this.category = category;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
    }

    public ProductDto(String productName,Double price ,String description, Integer quantity, String category) {
        this.category = category;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setId(long id){
        this.id = id;
    }

}
