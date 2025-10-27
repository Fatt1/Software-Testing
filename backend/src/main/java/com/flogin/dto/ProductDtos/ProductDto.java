package com.flogin.dto.ProductDtos;

public class ProductDto {

    private long id;
    private double price;
    private String productName;
    private  Integer quantity;
    private String description;
    private String category;


    public ProductDto(long id, String category, double price, String productName, String description, Integer quantity) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setId(long id){
        this.id = id;
    }

}
