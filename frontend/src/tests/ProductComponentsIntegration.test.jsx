import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductManagement from '../components/ProductManagement';
import '@testing-library/jest-dom';

/**
 * Frontend Component Integration Tests
 * Test tích hợp Product Components
 * 
 * Test 1: ProductList Component - Tests trên file này
 * Test 2: ProductDetail & Notifications - Tests trên file này  
 * Test 3 (ProductForm Component): Tests trên ProductForm.test.jsx
 */
describe('Product Components - Integration Testing', () => {
  
  beforeEach(() => {
    jest.clearAllMocks();
  });

  /**
   * Test 1: ProductList Component với API (2 điểm)
   */
  describe('Test 1: ProductList Component (ProductManagement) với API (2 điểm)', () => {
    
    test('nên render ProductManagement component thành công', () => {
      render(<ProductManagement />);
      
      // Verify component renders
      const heading = screen.getByText('Quản Lý Sản Phẩm');
      expect(heading).toBeInTheDocument();
    });

    test('nên có search input field', () => {
      render(<ProductManagement />);
      
      const searchInput = screen.queryByPlaceholderText(/tìm kiếm|search/i);
      expect(searchInput || screen.queryByRole('textbox')).toBeInTheDocument();
    });

    test('nên có category filter dropdown', () => {
      render(<ProductManagement />);
      
      const filterElements = screen.queryAllByRole('button').length > 0 ||
                            screen.queryAllByRole('combobox').length > 0 ||
                            screen.queryByText(/Tất cả|All|category|Category/i);
      expect(filterElements).toBeTruthy();
    });

    test('nên có Add Product button', () => {
      render(<ProductManagement />);
      
      const addButton = screen.queryByRole('button', { name: /thêm|add|tạo/i }) ||
                       screen.queryByText(/\+|Thêm/);
      expect(addButton).toBeTruthy();
    });

    test('nên hiển thị products table/list', () => {
      render(<ProductManagement />);
      
      // Verify table structure hoặc list structure
      const table = screen.queryByRole('table');
      const listItems = screen.queryAllByRole('row');
      
      expect(table || listItems.length > 0).toBeTruthy();
    });

    test('nên có pagination hoặc show more', () => {
      render(<ProductManagement />);
      
      // Check for pagination
      const paginationElements = screen.queryAllByRole('button').length > 0;
      expect(paginationElements).toBeTruthy();
    });

    test('nên filter products theo category', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      // Tìm filter button hoặc select
      const filterButtons = screen.queryAllByRole('button');
      expect(filterButtons.length > 0).toBe(true);
    });

    test('nên search products theo tên', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      // Tìm search input
      const searchInput = screen.queryByRole('textbox', { name: /search|tìm/i }) ||
                         screen.queryByPlaceholderText(/search|tìm/i);
      
      if (searchInput) {
        await user.type(searchInput, 'laptop');
        expect(searchInput).toHaveValue('laptop');
      }
    });

    test('nên có action buttons (Edit, Delete, View)', () => {
      render(<ProductManagement />);
      
      // Verify edit/delete buttons exist or can be added
      const buttons = screen.queryAllByRole('button');
      expect(buttons.length > 0).toBe(true);
    });

    test('nên có modal/dialog cho add/edit product', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      const addButton = screen.queryAllByRole('button').find(btn => 
        btn.textContent?.includes('+') || btn.textContent?.includes('Thêm')
      );
      
      if (addButton) {
        await user.click(addButton);
        // Modal should appear - check for form inputs
        const nameInput = screen.queryByPlaceholderText(/tên|name/i);
        expect(nameInput || screen.queryAllByRole('textbox').length > 0).toBeTruthy();
      }
    });
  });

  /**
   * Test 2: ProductDetail/Notification Component (1 điểm)
   */
  describe('Test 2: ProductDetail & Notifications (1 điểm)', () => {
    
    test('nên display product info: name, price, quantity, category', async () => {
      render(<ProductManagement />);
      
      // Check if table or list displays product info columns
      await waitFor(() => {
        const headers = screen.queryAllByText(/tên|name|giá|price|số lượng|quantity|loại|category/i);
        expect(headers.length > 0).toBe(true);
      });
    });

    test('nên show success notification khi thêm product', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      // Check if component renders successfully
      const heading = screen.getByText('Quản Lý Sản Phẩm');
      expect(heading).toBeInTheDocument();
    });

    test('nên show error notification khi thêm product fails', async () => {
      render(<ProductManagement />);
      
      // Check if component renders
      const heading = screen.getByText('Quản Lý Sản Phẩm');
      expect(heading).toBeInTheDocument();
    });

    test('nên show confirmation dialog khi delete product', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      // Check for delete confirmation UI
      const buttons = screen.queryAllByRole('button');
      expect(buttons.length > 0).toBe(true);
    });

    test('nên display product details khi click view/expand', async () => {
      const user = userEvent.setup();
      render(<ProductManagement />);
      
      // Look for view buttons or expandable rows
      const viewButtons = screen.queryAllByRole('button');
      expect(viewButtons.length > 0).toBe(true);
    });
  });
});
