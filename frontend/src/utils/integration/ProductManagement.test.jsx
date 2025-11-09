import { render, screen, fireEvent, waitFor, act, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { describe, test, expect, beforeEach, afterEach, jest } from '@jest/globals';
import ProductManagement from '../../components/ProductManagement.jsx';

// Mock localStorage
const mockLocalStorage = {
  storage: {},
  getItem(key) {
    return this.storage[key] || null;
  },
  setItem(key, value) {
    const v = typeof value === 'string' ? value : JSON.stringify(value);
    this.storage[key] = v;
    // mirror as own property so code using Object.keys(localStorage) can see keys
    this[key] = v;
  },
  removeItem(key) {
    delete this.storage[key];
  delete this[key];
  },
  clear() {
    this.storage = {};
    // remove mirrored keys
    Object.keys(this).forEach(k => {
      if (k.startsWith('product:')) delete this[k];
    });
  }
};

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
  writable: true
});

// Helper functions for finding buttons
const findSubmitButton = () => {
  const allButtons = screen.getAllByRole('button', { name: /thêm sản phẩm/i });
  return allButtons.find(button => 
    button.classList.contains('btn-primary') && 
    button.classList.contains('btn-full')
  );
};

const findAddButton = () => {
  const allButtons = screen.getAllByRole('button', { name: /thêm sản phẩm/i });
  return allButtons.find(button => 
    button.classList.contains('btn-primary') && 
    !button.classList.contains('btn-full')
  );
};

// Test utilities
const openAddProductModal = () => {
  const addButton = findAddButton();
  expect(addButton).toBeInTheDocument();
  fireEvent.click(addButton);
  return findSubmitButton();
};

describe('ProductManagement Integration Tests', () => {
  // Increase timeout for all tests
  jest.setTimeout(30000); // Increase global timeout to 30 seconds
  beforeEach(() => {
    localStorage.clear();
    // Use real timers to avoid interacting with component timeouts via fake timers
    // (component uses setTimeout for notifications)
  });

  afterEach(() => {
    // Clean up fake timers
    jest.useRealTimers();
  });

  test('renders product management interface with empty state', () => {
    render(<ProductManagement />);
    
    // Check main UI elements
    expect(screen.getByText('Quản Lý Sản Phẩm')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Tìm kiếm sản phẩm...')).toBeInTheDocument();
    expect(findAddButton()).toBeInTheDocument();
    
    // Check empty state
    expect(screen.getByText('Không có sản phẩm nào')).toBeInTheDocument();
  });

  test('can add a new product with valid data', async () => {
    jest.setTimeout(10000); // Increase timeout to 10 seconds
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addProductButton = findAddButton();
    expect(addProductButton).toBeInTheDocument();
    fireEvent.click(addProductButton);

    // Fill form with valid data
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Laptop Dell');
    await user.type(screen.getByLabelText(/giá/i), '15000000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Laptop Dell mới với cấu hình cao');

    // Find the submit button in the modal
    const submitButton = findSubmitButton();
    expect(submitButton).toBeTruthy();

    // Click the submit button
    act(() => {
      fireEvent.click(submitButton);
    });

    // Wait for success notification with increased timeout
    await waitFor(() => {
      const notification = screen.getByText('Thêm sản phẩm thành công!');
      expect(notification).toBeInTheDocument();
    }, { timeout: 2000 });

    // Verify product display immediately after successful submission
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const productRow = productRows.find(row => row.textContent.includes('Laptop Dell'));
      expect(productRow).toBeTruthy();
      expect(productRow.textContent).toContain('15.000.000 đ');
      expect(productRow.textContent).toContain('5');
      expect(productRow.textContent).toContain('Điện tử');
    }, { timeout: 2000 });

    // Wait for notification to disappear (notification auto-clears after 3s)
    await waitFor(() => {
      expect(screen.queryByText('Thêm sản phẩm thành công!')).not.toBeInTheDocument();
    }, { timeout: 5000 });

    // Verify product is still displayed
    expect(screen.getByRole('cell', { name: 'Laptop Dell' })).toBeInTheDocument();
  });

  test('displays validation errors for invalid form submission', async () => {
    render(<ProductManagement />);

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Find and click the submit button in the modal
    const submitButton = findSubmitButton();
    expect(submitButton).toBeTruthy();
    fireEvent.click(submitButton);

    // Check for validation errors
    await waitFor(() => {
      expect(screen.getByText('Tên sản phẩm không được để trống')).toBeInTheDocument();
      expect(screen.getByText('Giá không được để trống')).toBeInTheDocument();
      expect(screen.getByText('Số lượng không được để trống')).toBeInTheDocument();
      expect(screen.getByText('Vui lòng chọn danh mục')).toBeInTheDocument();
      expect(screen.getByText('Mô tả không được để trống')).toBeInTheDocument();
    }, { timeout: 1000 });
  });

  test('can edit an existing product', async () => {
    // Add initial product
    const initialProduct = {
      id: '1',
      name: 'Initial Product',
      price: '10000',
      quantity: '1',
      category: 'Điện tử',
      description: 'Initial description that is valid'
    };
    localStorage.setItem('product:1', JSON.stringify(initialProduct));

    render(<ProductManagement />);
    const user = userEvent.setup();

    // Wait for product to load and click edit
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const productText = productRows.some(row => row.textContent.includes('Initial Product'));
      expect(productText).toBeTruthy();
    }, { timeout: 2000 });

    const editButtons = screen.getAllByTitle('Chỉnh sửa');
    fireEvent.click(editButtons[0]);

    // Update product details
    const nameInput = screen.getByDisplayValue('Initial Product');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Product');

    // Save changes
    fireEvent.click(screen.getByRole('button', { name: /cập nhật/i }));

    // Verify updates
    await waitFor(() => {
      expect(screen.getByText('Cập nhật sản phẩm thành công!')).toBeInTheDocument();
    }, { timeout: 1000 });

    // Wait for the notification to disappear
    await waitFor(() => {
      expect(screen.queryByText('Cập nhật sản phẩm thành công!')).not.toBeInTheDocument();
    }, { timeout: 5000 });

    expect(screen.getByText('Updated Product')).toBeInTheDocument();
  });

  test('can delete a product', async () => {
    const user = userEvent.setup();
    // Add initial product
    const product = {
      id: '1',
      name: 'Product to Delete',
      price: '10000',
      quantity: '1',
      category: 'Điện tử',
      description: 'Product that will be deleted'
    };
    
    act(() => {
      localStorage.setItem('product:1', JSON.stringify(product));
    });

    render(<ProductManagement />);

    // Wait for product to load and click delete
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const productText = productRows.some(row => row.textContent.includes('Product to Delete'));
      expect(productText).toBeTruthy();
    }, { timeout: 2000 });

    const deleteButtons = screen.getAllByTitle('Xóa');
    await user.click(deleteButtons[0]);

  // Confirm deletion - pick the confirm dialog's destructive button (btn-danger)
  const confirmDeleteButtons = screen.getAllByRole('button', { name: /xóa/i });
  const confirmDeleteButton = confirmDeleteButtons.find(b => b.classList.contains('btn-danger'));
  expect(confirmDeleteButton).toBeTruthy();
  await user.click(confirmDeleteButton);

    // Verify deletion and notification
    await waitFor(() => {
      expect(screen.getByText('Xóa sản phẩm thành công!')).toBeInTheDocument();
    }, { timeout: 1000 });

    // Wait for notification to disappear
    await waitFor(() => {
      expect(screen.queryByText('Xóa sản phẩm thành công!')).not.toBeInTheDocument();
    }, { timeout: 5000 });

    // Verify product is no longer displayed
    expect(screen.queryByText('Product to Delete')).not.toBeInTheDocument();
  });

  test('search functionality filters products', async () => {
    // Add test products
    const products = [
      {
        id: '1',
        name: 'Laptop Dell',
        price: '15000000',
        quantity: '5',
        category: 'Điện tử',
        description: 'A Dell laptop'
      },
      {
        id: '2',
        name: 'Áo thun',
        price: '200000',
        quantity: '10',
        category: 'Thời trang',
        description: 'A nice shirt'
      }
    ];

    act(() => {
      products.forEach(product => {
        localStorage.setItem(`product:${product.id}`, JSON.stringify(product));
      });
    });

    render(<ProductManagement />);
    const user = userEvent.setup();

    // Wait for products to load with increased timeout
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const hasLaptop = productRows.some(row => row.textContent.includes('Laptop Dell'));
      const hasShirt = productRows.some(row => row.textContent.includes('Áo thun'));
      expect(hasLaptop).toBeTruthy();
      expect(hasShirt).toBeTruthy();
    }, { timeout: 5000 }); // Increase timeout to 5 seconds

    // Search for 'Laptop'
    const searchInput = screen.getByPlaceholderText('Tìm kiếm sản phẩm...');
    await user.type(searchInput, 'Laptop');

    // Verify filtered results
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const hasLaptop = productRows.some(row => row.textContent.includes('Laptop Dell'));
      const hasShirt = productRows.some(row => row.textContent.includes('Áo thun'));
      expect(hasLaptop).toBeTruthy();
      expect(hasShirt).toBeFalsy();
    }, { timeout: 2000 });
  });

  test('notification disappears after 3 seconds', async () => {
    render(<ProductManagement />);

    // Open add product modal and get the submit button
    const submitButton = openAddProductModal();

    // Submit empty form to trigger validation
    fireEvent.click(submitButton);

    // Check for validation error notification
    await waitFor(() => {
      expect(screen.getByText(/Vui lòng kiểm tra lại thông tin/)).toBeInTheDocument();
    }, { timeout: 1000 });

    // Wait for notification auto-dismiss (component clears it after 3s)
    await waitFor(() => {
      expect(screen.queryByText(/Vui lòng kiểm tra lại thông tin/)).not.toBeInTheDocument();
    }, { timeout: 5000 });
  });

  test('category filter works correctly', async () => {
    // Add test products in different categories
    const products = [
      {
        id: '1',
        name: 'Laptop Dell',
        price: '15000000',
        quantity: '5',
        category: 'Điện tử',
        description: 'A Dell laptop'
      },
      {
        id: '2',
        name: 'Áo thun',
        price: '200000',
        quantity: '10',
        category: 'Thời trang',
        description: 'A nice shirt'
      }
    ];

    // Add test products to localStorage
    act(() => {
      products.forEach(product => {
        localStorage.setItem(`product:${product.id}`, JSON.stringify(product));
      });
    });
    
    // Wait for any state updates
    await act(async () => {
      // Let any pending state updates complete
      await new Promise(resolve => setTimeout(resolve, 0));
    });

    render(<ProductManagement />);

    // Wait for products to load with a longer timeout
    await waitFor(() => {
      // Look for elements in the table cells
      const laptopElement = screen.getByRole('cell', { name: 'Laptop Dell' });
      const shirtElement = screen.getByRole('cell', { name: 'Áo thun' });
      expect(laptopElement).toBeInTheDocument();
      expect(shirtElement).toBeInTheDocument();
    }, { timeout: 2000 });

    // Filter by Electronics category
    const categoryFilter = screen.getByRole('combobox');
    await act(async () => {
      fireEvent.change(categoryFilter, { target: { value: 'Điện tử' } });
      // Let any state updates complete
      await new Promise(resolve => setTimeout(resolve, 0));
    });

    // Verify filtered results with a longer timeout
    await waitFor(() => {
      const productRows = screen.getAllByRole('row');
      const hasLaptop = productRows.some(row => row.textContent.includes('Laptop Dell'));
      const hasShirt = productRows.some(row => row.textContent.includes('Áo thun'));
      expect(hasLaptop).toBeTruthy();
      expect(hasShirt).toBeFalsy();
    }, { timeout: 2000 });
  });

  test('can view product details', async () => {
    // Add a product to localStorage
    const product = {
      id: '42',
      name: 'Product Detail Test',
      price: '10000',
      quantity: '1',
      category: 'Điện tử',
      description: 'Detailed description for testing'
    };

    act(() => {
      localStorage.setItem(`product:${product.id}`, JSON.stringify(product));
    });

    render(<ProductManagement />);
    const user = userEvent.setup();

    // Wait for the list to render the product
    await waitFor(() => {
      const rows = screen.getAllByRole('row');
      const hasProduct = rows.some(r => r.textContent.includes(product.name));
      expect(hasProduct).toBeTruthy();
    }, { timeout: 2000 });

    // Click the view (eye) button
    const viewButtons = screen.getAllByTitle('Xem chi tiết');
    expect(viewButtons.length).toBeGreaterThan(0);
    await user.click(viewButtons[0]);

    // Assert the detail modal appears with product info (query within the modal to avoid collisions)
    await waitFor(() => {
      const title = screen.getByText('Chi Tiết Sản Phẩm');
      expect(title).toBeInTheDocument();
      const modal = title.closest('.modal-content');
      expect(modal).toBeTruthy();
      const inside = within(modal);
      expect(inside.getByText(product.name)).toBeInTheDocument();
      expect(inside.getByText('Điện tử')).toBeInTheDocument();
      // price formatted
      expect(inside.getByText(/10.000/)).toBeInTheDocument();
      expect(inside.getByText(product.description)).toBeInTheDocument();

      // Modal action buttons
      expect(inside.getByRole('button', { name: /chỉnh sửa/i })).toBeInTheDocument();
      expect(inside.getByRole('button', { name: /đóng/i })).toBeInTheDocument();
    }, { timeout: 2000 });
  });

  // Test giá trị số âm
  test('displays error for negative price', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with negative price
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Test Product');
    await user.type(screen.getByLabelText(/giá/i), '-1000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Test description');

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Giá không được là số âm')).toBeInTheDocument();
    });
  });

  // Test số lượng âm
  test('displays error for negative quantity', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal 
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with negative quantity
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Test Product');
    await user.type(screen.getByLabelText(/giá/i), '10000');
    await user.type(screen.getByLabelText(/số lượng/i), '-5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Test description');

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Số lượng không được là số âm')).toBeInTheDocument();
    });
  });

  // Test độ dài tên sản phẩm
  test('displays error for product name exceeding max length', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with very long product name (over 100 characters)
    const longName = 'a'.repeat(101);
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), longName);
    await user.type(screen.getByLabelText(/giá/i), '10000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Test description');

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Tên sản phẩm không được vượt quá 100 ký tự')).toBeInTheDocument();
    });
  });

  // Test độ dài mô tả
  test('displays error for description exceeding max length', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with very long description (over 500 characters)
    const longDescription = 'a'.repeat(501);
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Test Product');
    await user.type(screen.getByLabelText(/giá/i), '10000');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), longDescription);

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Mô tả không được vượt quá 500 ký tự')).toBeInTheDocument();
    });
  });

  // Test giá trị không phải số
  test('displays error for non-numeric price', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with non-numeric price
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Test Product');
    await user.type(screen.getByLabelText(/giá/i), 'abc');
    await user.type(screen.getByLabelText(/số lượng/i), '5');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Test description');

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Giá phải là số')).toBeInTheDocument();
    });
  });

  // Test số lượng không phải số  
  test('displays error for non-numeric quantity', async () => {
    render(<ProductManagement />);
    const user = userEvent.setup();

    // Open add product modal
    const addButton = findAddButton();
    fireEvent.click(addButton);

    // Fill form with non-numeric quantity 
    await user.type(screen.getByPlaceholderText('Nhập tên sản phẩm'), 'Test Product');
    await user.type(screen.getByLabelText(/giá/i), '10000');
    await user.type(screen.getByLabelText(/số lượng/i), 'abc');
    await user.selectOptions(screen.getByRole('combobox', { name: /danh mục/i }), 'Điện tử');
    await user.type(screen.getByPlaceholderText('Nhập mô tả sản phẩm'), 'Test description');

    // Submit form
    const submitButton = findSubmitButton();
    fireEvent.click(submitButton);

    // Check for error message
    await waitFor(() => {
      expect(screen.getByText('Số lượng phải là số')).toBeInTheDocument();
    });
  });
});