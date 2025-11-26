package com.flogin.entity;

import java.util.Arrays;

/**
 * Category Enum - Product categorization system
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 * @see Product
 */
public enum Category {
    /** Electronic devices, gadgets, computers, phones, etc. */
    ELECTRONICS("Electronics"),
    
    /** Books, magazines, novels, educational materials, etc. */
    BOOKS("Books"),
    
    /** Clothing, apparel, fashion items, accessories, etc. */
    CLOTHING("Clothing"),
    
    /** Toys, games, puzzles, entertainment products, etc. */
    TOYS("Toys"),
    
    /** Food, beverages, groceries, household consumables, etc. */
    GROCERIES("Groceries");

    /** String representation of the category for display and storage */
    private final String value;

    /**
     * Constructor for Category enum
     * 
     * @param value String representation of category
     */
    Category(String value) {
        this.value = value;
    }

    /**
     * Get string value of category
     * @return Category string value (e.g., "Electronics")
     */
    public String getValue() {
        return value;
    }

    /**
     * Convert string to Category enum (case-insensitive)
     * @param value String representation of category (case-insensitive)
     * @return Corresponding Category enum value
     * @throws IllegalArgumentException if value is null, empty, or not a valid category
     */
    public static Category fromString(String value) {
        // Validate input is not null or empty
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Category value cannot be empty");
        }

        // Search for matching category (case-insensitive)
        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(value.trim())) {
                return category;
            }
        }

        // No match found - throw exception with helpful message
        throw new IllegalArgumentException(
            "Category '" + value + "' is invalid. Valid values: " + getAllValidValues()
        );
    }

    /**
     * Validate if a string is a valid category
     * @param value String to validate
     * @return true if string is a valid category, false otherwise
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        return Arrays.stream(Category.values())
                .anyMatch(category -> category.getValue().equals(value));
    }

    /**
     * Get comma-separated list of all valid category values
     * @return Comma-separated string of all category values
     */
    public static String getAllValidValues() {
        StringBuilder sb = new StringBuilder();
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            sb.append(categories[i].value);
            if (i < categories.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Get string representation of this category
     * @return Category string value
     */
    @Override
    public String toString() {
        return this.value;
    }
}
