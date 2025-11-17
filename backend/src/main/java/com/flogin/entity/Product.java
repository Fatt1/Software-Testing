package com.flogin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    
    @Column(name = "price", nullable = false)
    private double price;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;

    // Constructor mặc định (bắt buộc cho JPA)
    public Product() {
    }

    public Product(long id, Category category, String description, Integer quantity, String productName, double price) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    // Constructor nhận String category và convert sang enum (để tương thích ngược)
    public Product(long id, String category, String description, Integer quantity, String productName, double price) {
        this.id = id;
        this.category = Category.fromString(category);
        this.description = description;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Setter nhận String và convert sang enum (để tương thích ngược)
    public void setCategory(String category) {
        this.category = Category.fromString(category);
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
