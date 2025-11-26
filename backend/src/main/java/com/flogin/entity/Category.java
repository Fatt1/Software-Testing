package com.flogin.entity;

import java.util.Arrays;

/**
 * Category Enum - Product categorization system
 * 
 * <p>This enum defines all valid product categories in the system.
 * Used by Product entity to ensure data integrity and type safety.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Type-safe category values</li>
 *   <li>String-to-enum conversion with validation</li>
 *   <li>Case-insensitive category matching</li>
 *   <li>Comprehensive error messages</li>
 *   <li>Utility methods for validation and listing</li>
 * </ul>
 * 
 * <p>Available Categories:</p>
 * <ul>
 *   <li>ELECTRONICS - Electronic devices, gadgets, and technology products</li>
 *   <li>BOOKS - Books, magazines, and reading materials</li>
 *   <li>CLOTHING - Clothing, apparel, and fashion items</li>
 *   <li>TOYS - Toys, games, and entertainment products</li>
 *   <li>GROCERIES - Food, beverages, and grocery items</li>
 * </ul>
 * 
 * <p>Usage Examples:</p>
 * <pre>
 * // Type-safe enum usage
 * Category category = Category.ELECTRONICS;
 * 
 * // Convert from string (case-insensitive)
 * Category cat = Category.fromString("electronics");
 * 
 * // Validate string
 * if (Category.isValid("Books")) {
 *     // Process valid category
 * }
 * 
 * // Get all valid values
 * String validCategories = Category.getAllValidValues();
 * // Returns: "Electronics, Books, Clothing, Toys, Groceries"
 * </pre>
 * 
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
     * 
     * <p>Returns the display/storage string for this category.
     * Used when converting enum to string for API responses or database storage.</p>
     * 
     * @return Category string value (e.g., "Electronics")
     */
    public String getValue() {
        return value;
    }

    /**
     * Convert string to Category enum (case-insensitive)
     * 
     * <p>This method performs case-insensitive matching to find the corresponding
     * Category enum value. Trims whitespace from input for robustness.</p>
     * 
     * <p>Conversion Process:</p>
     * <ol>
     *   <li>Validate input is not null or empty</li>
     *   <li>Trim whitespace from input</li>
     *   <li>Compare with each enum value (case-insensitive)</li>
     *   <li>Return matching enum or throw exception</li>
     * </ol>
     * 
     * <p>Examples:</p>
     * <pre>
     * Category.fromString("Electronics")  // Returns ELECTRONICS
     * Category.fromString("electronics")  // Returns ELECTRONICS (case-insensitive)
     * Category.fromString(" Books ")      // Returns BOOKS (trims whitespace)
     * Category.fromString("Invalid")      // Throws IllegalArgumentException
     * </pre>
     * 
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
     * 
     * <p>Checks if the provided string matches any Category enum value.
     * Returns false for null or invalid strings instead of throwing exception.</p>
     * 
     * <p>Use this method when you need to check validity without catching exceptions.</p>
     * 
     * <p>Examples:</p>
     * <pre>
     * Category.isValid("Electronics")  // Returns true
     * Category.isValid("InvalidCat")   // Returns false
     * Category.isValid(null)           // Returns false
     * </pre>
     * 
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
     * 
     * <p>Returns a human-readable string containing all valid category values,
     * useful for error messages and documentation.</p>
     * 
     * <p>Output format: "Electronics, Books, Clothing, Toys, Groceries"</p>
     * 
     * <p>Use cases:</p>
     * <ul>
     *   <li>Display in API error messages</li>
     *   <li>Show in form validation errors</li>
     *   <li>Documentation and help text</li>
     *   <li>Test assertions</li>
     * </ul>
     * 
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
     * 
     * <p>Returns the display value of the category.
     * Same as calling getValue().</p>
     * 
     * @return Category string value
     */
    @Override
    public String toString() {
        return this.value;
    }
}
