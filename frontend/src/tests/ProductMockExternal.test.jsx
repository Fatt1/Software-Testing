/**
 * @see ../components/ProductManagement.jsx
 * @see ../services/productService.js
 */

import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductManagement from '../components/ProductManagement';
import * as productService from '../services/productService'; 

jest.mock('../services/productService');

describe('Product Mock Tests', () => {
  
  // Mock Data
  const mockProductList = [
    { 
      id: 1, 
      name: 'Laptop Dell', 
      productName: 'Laptop Dell', // Thêm trường này cho chắc chắn
      title: 'Laptop Dell',
      price: 15000000, 
      quantity: 10, 
      category: 'Electronics',
      description: 'Mô tả sản phẩm mẫu'
    }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    productService.getAllProducts.mockResolvedValue(mockProductList);
  });

  // Helper function: Sửa lại để xử lý trường hợp có nhiều nút cùng tên
  const fillAndSubmitForm = async (user) => {
    // 1. Mở Modal
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    
    // 2. Điền Form
    await user.type(screen.getByLabelText(/tên sản phẩm/i), 'New Laptop');
    await user.type(screen.getByLabelText(/giá/i), '20000000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByLabelText(/danh mục/i), 'Electronics');
    await user.type(screen.getByLabelText(/mô tả/i), 'Mô tả test');
    
    // 3. Submit
    // Lấy tất cả các nút có tên Lưu/Thêm/Cập Nhật
    const submitBtns = screen.getAllByRole('button', { name: /Lưu|Thêm|Cập Nhật/i });
    // Chọn nút cuối cùng (thường là nút nằm trong Modal vừa mở ra)
    const submitBtn = submitBtns[submitBtns.length - 1]; 
    await user.click(submitBtn);
  };

  test('Mock: Get products (READ)', async () => {
    productService.getAllProducts.mockResolvedValue(mockProductList);
    render(<ProductManagement />);

    await waitFor(() => {
      expect(screen.getByText('Laptop Dell')).toBeInTheDocument();
      expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
    });
  });

  test('Mock: Create product thanh cong', async () => {
    const user = userEvent.setup();
    const newProductResponse = { id: 2, name: 'New Laptop', price: 20000000 };

    productService.createProduct.mockResolvedValue(newProductResponse);

    render(<ProductManagement />);
    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(productService.createProduct).toHaveBeenCalledWith(expect.objectContaining({
        productName: 'New Laptop',  
        price: '20000000'           
      }));
      
      expect(screen.getByText(/thành công/i)).toBeInTheDocument();
    });
  });

  test('Mock: Create product that bai (Failure Scenario)', async () => {
    const user = userEvent.setup();
    productService.createProduct.mockRejectedValue(new Error('Server Error'));

    render(<ProductManagement />);
    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(productService.createProduct).toHaveBeenCalledTimes(1);
      expect(screen.getByText(/Server Error/i)).toBeInTheDocument();
    });
  });

  test('Mock: Delete product', async () => {
    const user = userEvent.setup();
    productService.deleteProduct.mockResolvedValue({ success: true });

    render(<ProductManagement />);
    await waitFor(() => screen.getByText('Laptop Dell'));
    
    const deleteBtns = screen.getAllByTitle(/Xóa/i);
    await user.click(deleteBtns[0]);

    const confirmBtn = await screen.findByText('Xóa', { selector: 'button' });
    await user.click(confirmBtn);

    await waitFor(() => {
      expect(productService.deleteProduct).toHaveBeenCalledWith(1);
    });
  });

  test('Mock: Update product', async () => {
    const user = userEvent.setup();
    productService.updateProduct.mockResolvedValue({ success: true });

    render(<ProductManagement />);
    await waitFor(() => screen.getByText('Laptop Dell'));
    
    const editBtns = screen.getAllByTitle(/Chỉnh sửa/i); 
    await user.click(editBtns[0]);
    
    // 2. Sửa giá
    const priceInput = screen.getByLabelText(/giá/i);
    await user.clear(priceInput);
    await user.type(priceInput, '18000000');
    
    const submitBtns = screen.getAllByRole('button', { name: /Lưu|Cập nhật/i });
    await user.click(submitBtns[submitBtns.length - 1]);

    await waitFor(() => {
      expect(productService.updateProduct).toHaveBeenCalledWith(1, expect.objectContaining({
        price: '18000000'
      }));
    });
  });
});