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

  // Price validation (boundary tests)
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
    } else if (priceNum > 1000000000) {
      errors.price = 'Giá sản phẩm không được vượt quá 1 tỷ';
    }
  }

  // Quantity validation
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
    } else if (quantityNum > 10000) {
      errors.quantity = 'Số lượng không được vượt quá 10000';
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

  // Category validation
  const validCategories = ['Electronics', 'Clothing', 'Food', 'Books', 'Other'];
  if (!product.category || product.category.trim() === '') {
    errors.category = 'Danh mục không được để trống';
  } else if (!validCategories.includes(product.category)) {
    errors.category = 'Danh mục không hợp lệ';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};