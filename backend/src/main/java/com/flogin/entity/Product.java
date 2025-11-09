package com.flogin.entity;

import java.math.BigDecimal;

public class Product {
    private long id;
    private double price;
    private String productName;
    private  Integer quantity;
    private String description;
    private String category;

    public Product(long id, String category, String description, Integer quantity, String productName, double price) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public long getId() {
        return id;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
