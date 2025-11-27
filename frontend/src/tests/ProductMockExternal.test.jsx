/**
 * @see ../components/ProductManagement.jsx
 * @see ../services/productService.js
 */

import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductManagement from '../components/ProductManagement';
import * as productService from '../services/productService'; // Import tất cả export

// Bước quan trọng: Mock module service
jest.mock('../services/productService');

describe('Product Mock Tests', () => {
  
  // Setup data mẫu dùng chung
  const mockProductList = [
    { id: 1, name: 'Laptop Dell', price: 15000000, quantity: 10, category: 'Điện tử' }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    // Default mock cho hàm get (để component không bị lỗi khi render)
    productService.getAllProducts.mockResolvedValue(mockProductList);
  });

  // Helper để điền form nhanh (cho gọn code test)
  const fillAndSubmitForm = async (user) => {
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    await user.type(screen.getByLabelText(/tên sản phẩm/i), 'New Laptop');
    await user.type(screen.getByLabelText(/giá/i), '20000000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByLabelText(/danh mục/i), 'Điện tử');
    await user.type(screen.getByLabelText(/mô tả/i), 'Mô tả test');
    await user.click(screen.getByRole('button', { name: /Lưu/i }));
  };

  /**
   * Yêu cầu (a): Mock CRUD - READ
   * Yêu cầu (c): Verify mock calls
   */
  test('Mock: Get products (READ)', async () => {
    // Setup Mock
    productService.getAllProducts.mockResolvedValue(mockProductList);

    // Test Implementation
    render(<ProductManagement />);

    // Verify
    await waitFor(() => {
      expect(screen.getByText('Laptop Dell')).toBeInTheDocument();
      // Verify (c)
      expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
    });
  });

  /**
   * Yêu cầu (a): Mock CRUD - CREATE
   * Yêu cầu (b): Success scenario
   * Yêu cầu (c): Verify calls with args
   */
  test('Mock: Create product thanh cong', async () => {
    const user = userEvent.setup();
    const newProductResponse = { id: 2, name: 'New Laptop', price: 20000000 };

    // Setup Mock (b - Success)
    productService.createProduct.mockResolvedValue(newProductResponse);

    // Test Implementation
    render(<ProductManagement />);
    await fillAndSubmitForm(user);

    // Verify (c)
    await waitFor(() => {
      // Kiểm tra hàm được gọi với đúng tham số
      expect(productService.createProduct).toHaveBeenCalledWith(expect.objectContaining({
        name: 'New Laptop',
        price: 20000000
      }));
      // Kiểm tra UI báo thành công
      expect(screen.getByText(/thành công/i)).toBeInTheDocument();
    });
  });

  /**
   * Yêu cầu (b): Failure scenario
   */
  test('Mock: Create product that bai (Failure Scenario)', async () => {
    const user = userEvent.setup();

    // Setup Mock (b - Failure)
    productService.createProduct.mockRejectedValue(new Error('Server Error'));

    // Test Implementation
    render(<ProductManagement />);
    await fillAndSubmitForm(user);

    // Verify
    await waitFor(() => {
      expect(productService.createProduct).toHaveBeenCalledTimes(1);
      // Kiểm tra UI báo lỗi
      expect(screen.getByText(/Server Error/i)).toBeInTheDocument();
    });
  });

  /**
   * Yêu cầu (a): Mock CRUD - DELETE
   */
  test('Mock: Delete product', async () => {
    const user = userEvent.setup();
    
    // Setup Mock
    productService.deleteProduct.mockResolvedValue({ success: true });

    // Test Implementation
    render(<ProductManagement />);
    // Chờ sản phẩm hiện ra rồi bấm xóa
    await waitFor(() => screen.getByText('Laptop Dell'));
    
    // Tìm và bấm nút xóa (giả sử nút xóa đầu tiên)
    const deleteBtns = screen.getAllByText(/Xóa/i);
    await user.click(deleteBtns[0]);

    // Verify (c)
    await waitFor(() => {
      expect(productService.deleteProduct).toHaveBeenCalledWith(1);
    });
  });

  /**
   * Yêu cầu (a): Mock CRUD - UPDATE
   */
  test('Mock: Update product', async () => {
    const user = userEvent.setup();
    
    // Setup Mock
    productService.updateProduct.mockResolvedValue({ success: true });

    // Test Implementation
    render(<ProductManagement />);
    await waitFor(() => screen.getByText('Laptop Dell'));
    
    // Bấm sửa -> Sửa giá -> Lưu
    const editBtns = screen.getAllByText(/Sửa/i);
    await user.click(editBtns[0]);
    
    const priceInput = screen.getByLabelText(/giá/i);
    await user.clear(priceInput);
    await user.type(priceInput, '18000000');
    
    await user.click(screen.getByRole('button', { name: /Lưu/i }));

    // Verify (c)
    await waitFor(() => {
      expect(productService.updateProduct).toHaveBeenCalledWith(1, expect.objectContaining({
        price: 18000000
      }));
    });
  });

});