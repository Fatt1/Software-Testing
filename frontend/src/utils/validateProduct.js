/**
 * Validate product data for create/update operations
 * 
 * Performs comprehensive validation of all product fields according to business rules.
 * Returns detailed error messages for each invalid field.
 * 
 * Validation Rules:
 * - name: 3-100 characters, required
 * - price: Positive number, 0 < price <= 999,999,999
 * - quantity: Non-negative integer, 0 <= quantity <= 99,999
 * - description: 10-500 characters, required
 * - category: Must be one of valid enum values
 * 
 * Valid Categories:
 * - Electronics
 * - Clothing
 * - Food
 * - Books
 * - Other
 * 
 * @function validateProduct
 * @param {Object} product - Product data object to validate
 * @param {string} product.name - Product name
 * @param {number} product.price - Product price
 * @param {number} product.quantity - Product quantity
 * @param {string} product.description - Product description
 * @param {string} product.category - Product category
 * @returns {Object} Validation result
 * @returns {boolean} return.isValid - True if all validations pass
 * @returns {Object} return.errors - Object containing error messages for each invalid field
 * 
 * @example
 * // Valid product
 * const result = validateProduct({
 *   name: 'iPhone 15',
 *   price: 999.99,
 *   quantity: 50,
 *   description: 'Latest iPhone model with advanced features',
 *   category: 'Electronics'
 * });
 * // Returns: { isValid: true, errors: {} }
 * 
 * @example
 * // Invalid product (multiple errors)
 * const result = validateProduct({
 *   name: 'ab',  // Too short
 *   price: -10,  // Negative
 *   quantity: 1.5,  // Not integer
 *   description: 'Short',  // Too short
 *   category: 'InvalidCat'  // Invalid category
 * });
 * // Returns: {
 * //   isValid: false,
 * //   errors: {
 * //     name: 'Tên sản phẩm phải có ít nhất 3 ký tự',
 * //     price: 'Giá sản phẩm không được âm',
 * //     quantity: 'Số lượng phải là số nguyên',
 * //     description: 'Mô tả phải có ít nhất 10 ký tự',
 * //     category: 'Danh mục không hợp lệ'
 * //   }
 * // }
 */
export const validateProduct = (product) => {
  const errors = {};

  // Product name validation
  if (!product.name || product.name.trim() === '') {
    errors.name = 'Tên sản phẩm không được để trống';
  } else if (product.name.trim().length < 3) {
    errors.name = 'Tên sản phẩm phải có ít nhất 3 ký tự';
  } else if (product.name.trim().length > 100) {
    errors.name = 'Tên sản phẩm không được vượt quá 100 ký tự';
  }

  // Price validation (boundary tests for positive numbers)
  if (product.price === undefined || product.price === null || product.price === '') {
    errors.price = 'Giá sản phẩm không được để trống';
  } else {
    const priceNum = Number(product.price);
    if (isNaN(priceNum)) {
      errors.price = 'Giá sản phẩm phải là số';
    } else if (priceNum < 0) {
      errors.price = 'Giá sản phẩm không được âm';
    } else if (priceNum === 0) {
      errors.price = 'Giá sản phẩm phải lớn hơn 0';
    } else if (priceNum > 999999999) {
      errors.price = 'Giá sản phẩm không được vượt quá 999,999,999';
    }
  }

  // Quantity validation (must be non-negative integer)
  if (product.quantity === undefined || product.quantity === null || product.quantity === '') {
    errors.quantity = 'Số lượng không được để trống';
  } else {
    const quantityNum = Number(product.quantity);
    if (isNaN(quantityNum)) {
      errors.quantity = 'Số lượng phải là số';
    } else if (!Number.isInteger(quantityNum)) {
      errors.quantity = 'Số lượng phải là số nguyên';
    } else if (quantityNum < 0) {
      errors.quantity = 'Số lượng không được âm';
    } else if (quantityNum > 99999) {
      errors.quantity = 'Số lượng không được vượt quá 99,999';
    }
  }

  // Description length validation
  if (!product.description || product.description.trim() === '') {
    errors.description = 'Mô tả không được để trống';
  } else if (product.description.trim().length < 10) {
    errors.description = 'Mô tả phải có ít nhất 10 ký tự';
  } else if (product.description.trim().length > 500) {
    errors.description = 'Mô tả không được vượt quá 500 ký tự';
  }

  // Category validation (must be one of predefined enum values)
  const validCategories = ['Electronics', 'Clothing', 'Food', 'Books', 'Other'];
  if (!product.category || product.category.trim() === '') {
    errors.category = 'Danh mục không được để trống';
  } else if (!validCategories.includes(product.category)) {
    errors.category = 'Danh mục không hợp lệ';
  }

  // Return validation result
  // isValid is true only if no errors were found
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};