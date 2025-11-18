import { validateProduct } from '../utils/validateProduct.js';
import { describe, test, expect } from '@jest/globals';


describe('validateProduct - Kiểm tra tên sản phẩm', () => {
  test('trả về lỗi khi tên rỗng', () => {
    const product = { name: '', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.name).toBe('Tên sản phẩm không được để trống');
  });

  test('trả về lỗi khi tên chỉ là khoảng trắng', () => {
    const product = { name: '   ', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.name).toBe('Tên sản phẩm không được để trống');
  });

  test('trả về lỗi khi tên ít hơn 3 ký tự', () => {
    const product = { name: 'AB', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.name).toBe('Tên sản phẩm phải có ít nhất 3 ký tự');
  });

  test('hợp lệ khi tên đúng 3 ký tự', () => {
    const product = { name: 'ABC', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.name).toBeUndefined();
  });

  test('trả về lỗi khi tên vượt quá 100 ký tự', () => {
    const product = { 
      name: 'A'.repeat(101), 
      price: 100, 
      quantity: 10, 
      description: 'Valid description', 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.name).toBe('Tên sản phẩm không được vượt quá 100 ký tự');
  });

  test('hợp lệ khi tên đúng 100 ký tự', () => {
    const product = { 
      name: 'A'.repeat(100), 
      price: 100, 
      quantity: 10, 
      description: 'Valid description', 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.errors.name).toBeUndefined();
  });

  test('hợp lệ khi tên hợp lệ', () => {
    const product = { name: 'Valid Product Name', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.name).toBeUndefined();
  });
});

describe('validateProduct - Kiểm tra giá (kiểm thử biên)', () => {
  test('trả về lỗi khi giá là undefined', () => {
    const product = { name: 'Product', price: undefined, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm không được để trống');
  });

  test('trả về lỗi khi giá là null', () => {
    const product = { name: 'Product', price: null, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm không được để trống');
  });

  test('trả về lỗi khi giá là chuỗi rỗng', () => {
    const product = { name: 'Product', price: '', quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm không được để trống');
  });

  test('trả về lỗi khi giá không phải số', () => {
    const product = { name: 'Product', price: 'abc', quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm phải là số');
  });

  test('trả về lỗi khi giá âm', () => {
    const product = { name: 'Product', price: -100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm không được âm');
  });

  test('trả về lỗi khi giá bằng 0', () => {
    const product = { name: 'Product', price: 0, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm phải lớn hơn 0');
  });

  test('hợp lệ khi giá là 0.01 (giá trị nhỏ nhất hợp lệ)', () => {
    const product = { name: 'Product', price: 0.01, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.price).toBeUndefined();
  });

  test('hợp lệ khi giá bằng 999999999 (giá trị lớn nhất hợp lệ)', () => {
    const product = { name: 'Product', price: 999999999, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.price).toBeUndefined();
  });

  test('trả về lỗi khi giá vượt quá 999999999', () => {
    const product = { name: 'Product', price: 1000000000, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.price).toBe('Giá sản phẩm không được vượt quá 999,999,999');
  });

  test('hợp lệ khi giá hợp lệ (trường hợp điển hình)', () => {
    const product = { name: 'Product', price: 50000, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.price).toBeUndefined();
  });
});

describe('validateProduct - Kiểm tra số lượng', () => {
  test('trả về lỗi khi số lượng là undefined', () => {
    const product = { name: 'Product', price: 100, quantity: undefined, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng không được để trống');
  });

  test('trả về lỗi khi số lượng là null', () => {
    const product = { name: 'Product', price: 100, quantity: null, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng không được để trống');
  });

  test('trả về lỗi khi số lượng là chuỗi rỗng', () => {
    const product = { name: 'Product', price: 100, quantity: '', description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng không được để trống');
  });

  test('trả về lỗi khi số lượng không phải số', () => {
    const product = { name: 'Product', price: 100, quantity: 'abc', description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng phải là số');
  });

  test('trả về lỗi khi số lượng không phải số nguyên', () => {
    const product = { name: 'Product', price: 100, quantity: 10.5, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng phải là số nguyên');
  });

  test('trả về lỗi khi số lượng âm', () => {
    const product = { name: 'Product', price: 100, quantity: -5, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng không được âm');
  });

  test('hợp lệ khi số lượng là 0', () => {
    const product = { name: 'Product', price: 100, quantity: 0, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.quantity).toBeUndefined();
  });

  test('hợp lệ khi số lượng đúng 99999 (giá trị lớn nhất hợp lệ)', () => {
    const product = { name: 'Product', price: 100, quantity: 99999, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.quantity).toBeUndefined();
  });

  test('trả về lỗi khi số lượng vượt quá 99999', () => {
    const product = { name: 'Product', price: 100, quantity: 100000, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.quantity).toBe('Số lượng không được vượt quá 99,999');
  });

  test('hợp lệ khi số lượng là số nguyên hợp lệ', () => {
    const product = { name: 'Product', price: 100, quantity: 50, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.quantity).toBeUndefined();
  });
});

describe('validateProduct - Kiểm tra độ dài mô tả', () => {
  test('trả về lỗi khi mô tả rỗng', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: '', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.description).toBe('Mô tả không được để trống');
  });

  test('trả về lỗi khi mô tả chỉ là khoảng trắng', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: '   ', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.description).toBe('Mô tả không được để trống');
  });

  test('trả về lỗi khi mô tả ít hơn 10 ký tự', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Short', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.description).toBe('Mô tả phải có ít nhất 10 ký tự');
  });

  test('hợp lệ khi mô tả đúng 10 ký tự', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: '1234567890', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.description).toBeUndefined();
  });

  test('hợp lệ khi mô tả đúng 500 ký tự', () => {
    const product = { 
      name: 'Product', 
      price: 100, 
      quantity: 10, 
      description: 'A'.repeat(500), 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.errors.description).toBeUndefined();
  });

  test('trả về lỗi khi mô tả vượt quá 500 ký tự', () => {
    const product = { 
      name: 'Product', 
      price: 100, 
      quantity: 10, 
      description: 'A'.repeat(501), 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.description).toBe('Mô tả không được vượt quá 500 ký tự');
  });

  test('hợp lệ khi mô tả hợp lệ', () => {
    const product = { 
      name: 'Product', 
      price: 100, 
      quantity: 10, 
      description: 'This is a valid product description', 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.errors.description).toBeUndefined();
  });
});

describe('validateProduct - Kiểm tra danh mục', () => {
  test('trả về lỗi khi danh mục rỗng', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: '' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.category).toBe('Danh mục không được để trống');
  });

  test('trả về lỗi khi danh mục chỉ là khoảng trắng', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: '   ' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.category).toBe('Danh mục không được để trống');
  });

  test('trả về lỗi khi danh mục không hợp lệ', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'InvalidCategory' };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.category).toBe('Danh mục không hợp lệ');
  });

  test('hợp lệ khi danh mục là Electronics', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'Electronics' };
    const result = validateProduct(product);
    
    expect(result.errors.category).toBeUndefined();
  });

  test('hợp lệ khi danh mục là Clothing', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'Clothing' };
    const result = validateProduct(product);
    
    expect(result.errors.category).toBeUndefined();
  });

  test('hợp lệ khi danh mục là Food', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'Food' };
    const result = validateProduct(product);
    
    expect(result.errors.category).toBeUndefined();
  });

  test('hợp lệ khi danh mục là Books', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'Books' };
    const result = validateProduct(product);
    
    expect(result.errors.category).toBeUndefined();
  });

  test('hợp lệ khi danh mục là Other', () => {
    const product = { name: 'Product', price: 100, quantity: 10, description: 'Valid description', category: 'Other' };
    const result = validateProduct(product);
    
    expect(result.errors.category).toBeUndefined();
  });
});

describe('validateProduct - Kiểm tra tích hợp', () => {
  test('trả về isValid true khi tất cả trường hợp hợp lệ', () => {
    const product = { 
      name: 'Valid Product', 
      price: 100, 
      quantity: 10, 
      description: 'This is a valid description', 
      category: 'Electronics' 
    };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(true);
    expect(result.errors).toEqual({});
  });

  test('trả về nhiều lỗi khi nhiều trường không hợp lệ', () => {
    const product = { 
      name: '', 
      price: -100, 
      quantity: 'abc', 
      description: 'Short', 
      category: '' 
    };
    const result = validateProduct(product);
    
    expect(result.isValid).toBe(false);
    expect(result.errors.name).toBeDefined();
    expect(result.errors.price).toBeDefined();
    expect(result.errors.quantity).toBeDefined();
    expect(result.errors.description).toBeDefined();
    expect(result.errors.category).toBeDefined();
  });
});