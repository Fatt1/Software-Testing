package com.flogin.entity;

/**
 * Enum Category cho Product
 * Định nghĩa các danh mục sản phẩm hợp lệ trong hệ thống
 */
public enum Category {
    ELECTRONICS("Electronics"),
    BOOKS("Books"),
    CLOTHING("Clothing"),
    TOYS("Toys"),
    GROCERIES("Groceries");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Chuyển đổi từ String sang Category enum
     */
    public static Category fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Category value cannot be empty");
        }

        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(value.trim())) {
                return category;
            }
        }

        throw new IllegalArgumentException(
            "Category '" + value + "' is invalid. Valid values: " + getAllValidValues()
        );
    }

    /**
     * Kiểm tra xem một string có phải là category hợp lệ không
     */
    public static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy danh sách tất cả các giá trị category hợp lệ
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

    @Override
    public String toString() {
        return this.value;
    }
}
