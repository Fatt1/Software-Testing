# Tổng Hợp Nhiệm Vụ Được Giao (Đức)

File này giúp Đức trả lời miệng tự tin: mình làm gì, ở lớp test nào, vì sao làm như vậy, và nếu bị hỏi sâu thì có câu trả lời ngắn gọn rõ ràng.

## 1. Phạm Vi Chính Đức Phụ Trách

### 1.1 Product - Phân Tích và Test Scenarios (Câu 1.2) - **10 điểm**
- **File chính**: `docs/TEST_CASE_MATRIX_PRODUCT.md` (có sẵn) hoặc mở rộng
- **Mục tiêu**: Viết kịch bản test toàn diện cho tất cả feature Product
  - Test scenarios cho CRUD operations (Create, Read, Update, Delete)
  - Test scenarios cho Search & Filter
  - Test scenarios cho Pagination
  - Test scenarios cho Validation
  - Test scenarios cho Error handling

### 1.2 Frontend Component Integration Testing (Câu 3.2) - **5 điểm**
- **File chính**: `frontend/src/tests/ProductComponentsIntegration.test.jsx` (có sẵn, cần extend)
- **Mục tiêu**: Test render + tương tác UI của ProductManagement component
  - Kiểm tra component render đúng
  - Kiểm tra form open/close modal
  - Kiểm tra button click handlers
  - Kiểm tra state management
  - Kiểm tra input field updates
  - Kiểm tra table display

### 1.3 Performance Testing - Load/Stress Test (Bonus) - **5 điểm**
- **File chính**: `performance-tests/scripts/product-test.js` (có sẵn) hoặc mở rộng
- **Mục tiêu**: Thiết lập k6 performance test cho Product API
  - Load test: 100, 500, 1000 concurrent users
  - Measure response time
  - Measure error rate
  - Stress test: tìm breaking point

### 1.4 Security Testing - Input Sanitization (Bonus) - **5 điểm**
- **File chính**: (new file) `frontend/src/tests/security/sanitization.test.js`
- **Mục tiêu**: Kiểm tra input validation và sanitization
  - Test XSS payload sanitization
  - Test HTML entity encoding
  - Test special character escaping
  - Test length validation enforcement

## 2. Mapping Nhiệm Vụ → File Cụ Thể & Điểm

| Nhiệm vụ | Loại test | File chính | Điểm | Status |
|----------|-----------|------------|------|--------|
| Product Test Scenarios | Analysis | TEST_CASE_MATRIX_PRODUCT.md | 10 | Có sẵn, cần extend |
| Component Integration | Integration | ProductComponentsIntegration.test.jsx | 5 | Có sẵn, cần extend |
| Performance Testing | Performance | product-test.js + k6 | 5 | Bonus |
| Sanitization Testing | Security | sanitization.test.js | 5 | Bonus |
| **Tổng điểm chính** | | | **15** | |
| **Tổng với bonus** | | | **25** | |

## 3. Chi Tiết Từng Phần Công Việc

### 3.1 Product - Phân Tích và Test Scenarios (10 điểm)

**Tình trạng hiện tại**: File `TEST_CASE_MATRIX_PRODUCT.md` có sẵn (~30 test cases).

**Cần làm**:
- Review toàn bộ test case matrix
- Thêm test cases cho edge cases
- Đảm bảo traceability (requirement → test case)
- Định rõ expected result cho mỗi test case

**Test Scenarios Framework**:

#### Scenario Set 1: Create Product (SC1 - 6 test cases)
| TC ID | Test Case | Input | Expected |
|-------|-----------|-------|----------|
| SC1.1 | Create with valid data | All fields valid | 201 Created, product in list |
| SC1.2 | Create with missing name | Name = "" | 400 Bad Request, show error |
| SC1.3 | Create with short name | Name = "ab" | 400, validation error |
| SC1.4 | Create with invalid price | Price = 0 | 400, price error |
| SC1.5 | Create with invalid quantity | Quantity = -5 | 400, quantity error |
| SC1.6 | Create with invalid category | Category not in list | 400, category error |

#### Scenario Set 2: Read/Display Product (SC2 - 6 test cases)
| TC ID | Test Case | Condition | Expected |
|-------|-----------|-----------|----------|
| SC2.1 | Display empty list | No products | Show "Không có sản phẩm" message |
| SC2.2 | Display product list | 5 products in DB | All 5 products in table |
| SC2.3 | Display pagination | 15 products, page size 5 | Show page 1-3, 5 items each |
| SC2.4 | Get non-existent product | ID = 999 | 404 Not Found |
| SC2.5 | Display product details | Click row | Modal shows full info |
| SC2.6 | Display edit form | Click Edit button | Form pre-filled with data |

#### Scenario Set 3: Update Product (SC3 - 6 test cases)
| TC ID | Test Case | Input | Expected |
|-------|-----------|-------|----------|
| SC3.1 | Update with valid data | All fields updated | 200 OK, product updated in list |
| SC3.2 | Update name only | Name changed | 200 OK, other fields unchanged |
| SC3.3 | Update price only | Price changed | 200 OK, validate > 0 |
| SC3.4 | Update with invalid data | Name = "" | 400, validation error |
| SC3.5 | Update non-existent product | ID = 999 | 404 Not Found |
| SC3.6 | Update with duplicate name | Name already exists | Warning or allow with timestamp? |

#### Scenario Set 4: Delete Product (SC4 - 4 test cases)
| TC ID | Test Case | Action | Expected |
|-------|-----------|--------|----------|
| SC4.1 | Delete with confirm | Click Yes | 200 OK, product removed from list |
| SC4.2 | Delete cancel | Click No | Product remains in list |
| SC4.3 | Delete non-existent | ID = 999 | 404 Not Found |
| SC4.4 | Delete last product | Delete only product | List shows empty message |

#### Scenario Set 5: Search & Filter (SC5 - 8 test cases)
| TC ID | Test Case | Input | Expected |
|-------|-----------|-------|----------|
| SC5.1 | Search by name | Name = "Laptop" | Show only matching products |
| SC5.2 | Search case insensitive | Name = "LAPTOP" | Match "laptop" products |
| SC5.3 | Search partial | Name = "top" | Match products containing "top" |
| SC5.4 | Search empty results | Name = "xyz999" | Show no results message |
| SC5.5 | Filter by category | Category = "Electronics" | Show only Electronics |
| SC5.6 | Search + Filter combined | Name = "Laptop" + Category = "Electronics" | Show matching both |
| SC5.7 | Clear search | Clear input | Show all products |
| SC5.8 | Clear filter | Reset category to "all" | Show all products |

#### Scenario Set 6: Pagination (SC6 - 4 test cases)
| TC ID | Test Case | Action | Expected |
|-------|-----------|--------|----------|
| SC6.1 | Next page | 15 items, page 1 | Show items 6-10 on page 2 |
| SC6.2 | Previous page | On page 2 | Go to page 1 |
| SC6.3 | Go to page | Page 3 | Show items 11-15 |
| SC6.4 | Pagination with search | Search + pagination | Filtered items paginated |

#### Scenario Set 7: Validation & Error Handling (SC7 - 6 test cases)
| TC ID | Test Case | Input | Expected |
|-------|-----------|-------|----------|
| SC7.1 | Name > 100 chars | 101 chars | Validation error |
| SC7.2 | Price negative | -10.50 | Validation error |
| SC7.3 | Quantity negative | -1 | Validation error |
| SC7.4 | API timeout | Server no response > 5s | Show timeout error |
| SC7.5 | Network error | Connection lost | Show retry button |
| SC7.6 | Duplicate submission | Double-click create | Only create once |

**Output Format**:
```markdown
## Test Case Matrix - Product Management

### Scenario 1: Create Product (6 test cases)
| TC ID | Description | Input | Expected Result | Priority |
|-------|-------------|-------|-----------------|----------|
| SC1.1 | Create valid product | name="Laptop", price=999 | 201 Created | HIGH |
| SC1.2 | Reject empty name | name="" | 400 Bad Request | HIGH |
| ... | ... | ... | ... | ... |

### Scenario 2: Read Product (6 test cases)
...

### Traceability Matrix
| Requirement | Test Case | Component |
|-------------|-----------|-----------|
| User can create product | SC1.1, SC1.2 | Backend + Frontend |
| ...
```

---

### 3.2 Frontend Component Integration Testing (5 điểm)

**Tình trạng hiện tại**: File `ProductComponentsIntegration.test.jsx` có sẵn nhưng chưa đầy đủ.

**File**: `frontend/src/tests/ProductComponentsIntegration.test.jsx`

**Test cases cần viết/extend**:

#### Test Set 1: Component Rendering (1 điểm)
```javascript
describe('ProductManagement Component - Rendering', () => {
  test('nên render component thành công', () => {
    render(<ProductManagement />);
    expect(screen.getByText(/Quản Lý Sản Phẩm/i)).toBeInTheDocument();
  });

  test('nên hiển thị bảng sản phẩm', () => {
    render(<ProductManagement />);
    expect(screen.getByRole('table')).toBeInTheDocument();
  });

  test('nên hiển thị nút "Thêm Sản Phẩm"', () => {
    render(<ProductManagement />);
    expect(screen.getByText(/Thêm Sản Phẩm|Add Product/i)).toBeInTheDocument();
  });

  test('nên hiển thị search input', () => {
    render(<ProductManagement />);
    expect(screen.getByPlaceholderText(/Tìm kiếm|Search/i)).toBeInTheDocument();
  });

  test('nên hiển thị category filter dropdown', () => {
    render(<ProductManagement />);
    expect(screen.getByDisplayValue(/Tất cả|All/i)).toBeInTheDocument();
  });

  test('nên hiển thị thông báo "Không có sản phẩm" khi danh sách rỗng', async () => {
    render(<ProductManagement />);
    await waitFor(() => {
      expect(screen.queryByText(/Không có sản phẩm/i)).toBeInTheDocument();
    });
  });
});
```

#### Test Set 2: Modal Operations (1 điểm)
```javascript
describe('ProductManagement - Modal Operations', () => {
  test('nên mở modal tạo khi click "Thêm Sản Phẩm"', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    const addBtn = screen.getByText(/Thêm Sản Phẩm/i);
    await user.click(addBtn);
    
    expect(screen.getByText(/Thêm Sản Phẩm Mới|Create Product/i)).toBeInTheDocument();
  });

  test('nên đóng modal khi click nút X', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    // Open modal
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    
    // Close modal
    const closeBtn = screen.getByRole('button', { name: /X|Close/i });
    await user.click(closeBtn);
    
    expect(screen.queryByText(/Thêm Sản Phẩm Mới/i)).not.toBeInTheDocument();
  });

  test('nên clear form data khi đóng modal', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    // Open modal, type data
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const nameInput = screen.getByPlaceholderText(/Tên sản phẩm/i);
    await user.type(nameInput, 'Test Product');
    
    // Close modal
    await user.click(screen.getByRole('button', { name: /X/i }));
    
    // Reopen and check form is clear
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    expect(nameInput).toHaveValue('');
  });

  test('nên prefill form khi click Edit', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    // Wait for products load and find edit button
    const editBtn = await screen.findByRole('button', { name: /Edit|Sửa/i });
    await user.click(editBtn);
    
    // Modal should show with product data
    expect(screen.getByDisplayValue(/Laptop|Product Name/i)).toBeInTheDocument();
  });
});
```

#### Test Set 3: Form Validation (1 điểm)
```javascript
describe('ProductManagement - Form Validation', () => {
  test('nên show lỗi khi name < 3 ký tự', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const nameInput = screen.getByPlaceholderText(/Tên sản phẩm/i);
    
    await user.type(nameInput, 'ab');
    const submitBtn = screen.getByRole('button', { name: /Tạo|Save/i });
    await user.click(submitBtn);
    
    expect(screen.getByText(/ít nhất 3 ký tự/i)).toBeInTheDocument();
  });

  test('nên show lỗi khi price = 0', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const priceInput = screen.getByPlaceholderText(/Giá/i);
    
    await user.clear(priceInput);
    await user.type(priceInput, '0');
    await user.click(screen.getByRole('button', { name: /Tạo/i }));
    
    expect(screen.getByText(/phải.*0|must.*greater/i)).toBeInTheDocument();
  });

  test('nên show lỗi khi quantity < 0', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const qtyInput = screen.getByPlaceholderText(/Số lượng|Quantity/i);
    
    await user.type(qtyInput, '-5');
    await user.click(screen.getByRole('button', { name: /Tạo/i }));
    
    expect(screen.getByText(/không âm|must.*positive/i)).toBeInTheDocument();
  });

  test('nên disable submit button nếu có lỗi', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const nameInput = screen.getByPlaceholderText(/Tên sản phẩm/i);
    await user.type(nameInput, 'ab'); // < 3 chars
    
    const submitBtn = screen.getByRole('button', { name: /Tạo|Save/i });
    expect(submitBtn).toBeDisabled();
  });
});
```

#### Test Set 4: Search & Filter (1 điểm)
```javascript
describe('ProductManagement - Search & Filter', () => {
  test('nên filter products khi type search', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(/Laptop|Product/i)).toBeInTheDocument();
    });
    
    const searchInput = screen.getByPlaceholderText(/Tìm kiếm|Search/i);
    await user.type(searchInput, 'Laptop');
    
    expect(screen.getByText(/Laptop/i)).toBeInTheDocument();
    // Should not show non-matching products
  });

  test('nên filter by category', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    const categorySelect = screen.getByDisplayValue(/Tất cả|All/i);
    await user.selectOption(categorySelect, 'Electronics');
    
    await waitFor(() => {
      // Should show only Electronics products
    });
  });

  test('nên clear search results khi delete search text', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    const searchInput = screen.getByPlaceholderText(/Tìm kiếm/i);
    await user.type(searchInput, 'Laptop');
    await user.clear(searchInput);
    
    // Should show all products again
    expect(screen.queryByText(/Không có sản phẩm/i)).not.toBeInTheDocument();
  });
});
```

#### Test Set 5: Pagination & User Feedback (1 điểm)
```javascript
describe('ProductManagement - Pagination & Feedback', () => {
  test('nên show success notification khi tạo sản phẩm', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    // Mock API success
    jest.spyOn(productService, 'createProduct').mockResolvedValue({ id: 1 });
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const nameInput = screen.getByPlaceholderText(/Tên sản phẩm/i);
    await user.type(nameInput, 'New Product');
    
    await user.click(screen.getByRole('button', { name: /Tạo/i }));
    
    expect(screen.getByText(/thành công|success/i)).toBeInTheDocument();
  });

  test('nên show error notification khi API fail', async () => {
    const user = userEvent.setup();
    render(<ProductManagement />);
    
    jest.spyOn(productService, 'createProduct').mockRejectedValue(
      new Error('Server error')
    );
    
    await user.click(screen.getByText(/Thêm Sản Phẩm/i));
    const nameInput = screen.getByPlaceholderText(/Tên sản phẩm/i);
    await user.type(nameInput, 'New Product');
    
    await user.click(screen.getByRole('button', { name: /Tạo/i }));
    
    expect(screen.getByText(/lỗi|error/i)).toBeInTheDocument();
  });

  test('nên show pagination controls khi có > 5 products', async () => {
    render(<ProductManagement />);
    
    await waitFor(() => {
      if (screen.queryAllByRole('row').length > 6) { // 1 header + 5 data rows
        expect(screen.getByText(/Trang|Page/i)).toBeInTheDocument();
      }
    });
  });
});
```

**Summary**: ~150-200 lines of integration test code.

---

### 3.3 Performance Testing - Load/Stress Test (5 điểm BONUS)

**File**: `performance-tests/scripts/product-test.js` (mở rộng)

**Mục tiêu**: Kiểm tra performance API dưới tải cao.

**Test Scenarios**:

#### Load Test Script (k6)
```javascript
import http from 'k6/http';
import { check, group } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 100 },    // 0 → 100 users
    { duration: '1m30s', target: 100 },  // Stay at 100
    { duration: '30s', target: 0 },      // 100 → 0 users
  ],
  thresholds: {
    'http_req_duration': ['p(95)<800', 'p(99)<1000'],  // 95% < 800ms
    'http_req_failed': ['rate<0.1'],                    // < 10% error rate
  },
};

export default function () {
  group('Product GET List', function () {
    let response = http.get('http://localhost:8080/api/products?page=0&size=10');
    check(response, {
      'status is 200': (r) => r.status === 200,
      'response time < 800ms': (r) => r.timings.duration < 800,
      'body contains products': (r) => r.body.includes('productName'),
    });
  });

  group('Product GET by ID', function () {
    let response = http.get('http://localhost:8080/api/products/1');
    check(response, {
      'status is 200': (r) => r.status === 200,
    });
  });

  group('Product CREATE', function () {
    let payload = JSON.stringify({
      productName: 'Performance Test Product ' + __VU + '_' + __ITER,
      price: 99.99,
      quantity: 100,
      category: 'Electronics',
      description: 'Performance test product',
    });

    let response = http.post('http://localhost:8080/api/products', payload, {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, {
      'status is 201': (r) => r.status === 201,
      'response time < 1000ms': (r) => r.timings.duration < 1000,
    });
  });
}
```

#### Stress Test Script
```javascript
// Tìm breaking point: tăng users dần cho tới khi API fail

export const options = {
  stages: [
    { duration: '2m', target: 100 },    // Ramp-up
    { duration: '5m', target: 500 },    // Stress
    { duration: '10m', target: 1000 },  // Heavy load
    { duration: '5m', target: 1500 },   // Breaking point
    { duration: '2m', target: 0 },      // Ramp-down
  ],
  thresholds: {
    'http_req_duration': ['p(99)<2000'], // More lenient
    'http_req_failed': ['rate<0.5'],     // Allow more failures
  },
};

// ... rest of script
```

**Run Commands**:
```bash
# Load test 100 users
k6 run performance-tests/scripts/product-test.js

# Stress test with custom options
k6 run --vus 500 --duration 5m performance-tests/scripts/product-test.js

# Generate HTML report
k6 run --out json=results.json product-test.js
```

**Expected Output**:
- p95 response time < 800ms
- p99 response time < 1000ms
- Error rate < 1%
- Throughput: X requests/second

---

### 3.4 Security Testing - Input Sanitization (5 điểm BONUS)

**File**: `frontend/src/tests/security/sanitization.test.js` (new)

**Mục tiêu**: Kiểm tra input sanitization để ngăn chặn XSS.

```javascript
import { sanitizeInput, escapeHTML } from '../../utils/sanitization';
import { validateProduct } from '../../utils/validateProduct';

describe('Security - Input Sanitization', () => {
  
  describe('XSS Prevention', () => {
    test('nên reject <script> tag ở product name', () => {
      const result = validateProduct({
        productName: '<script>alert("XSS")</script>',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.errors.productName).toBeDefined();
      expect(result.isValid).toBe(false);
    });

    test('nên reject event handler injection ở description', () => {
      const result = validateProduct({
        productName: 'Product',
        price: 99.99,
        quantity: 10,
        category: 'Electronics',
        description: '" onmouseover="alert(1)"'
      });
      
      expect(result.isValid).toBe(false);
    });

    test('nên reject img onerror injection', () => {
      const result = validateProduct({
        productName: '<img src=x onerror="alert(1)">',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(false);
    });

    test('nên reject SVG script injection', () => {
      const result = validateProduct({
        productName: '<svg><script>alert(1)</script></svg>',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(false);
    });
  });

  describe('HTML Entity Encoding', () => {
    test('nên encode < và > characters', () => {
      const encoded = escapeHTML('<script>');
      expect(encoded).toBe('&lt;script&gt;');
    });

    test('nên encode quotes', () => {
      const encoded = escapeHTML('"hello"');
      expect(encoded).toBe('&quot;hello&quot;');
    });

    test('nên encode ampersand', () => {
      const encoded = escapeHTML('&');
      expect(encoded).toBe('&amp;');
    });

    test('nên handle multiple special chars', () => {
      const encoded = escapeHTML('<img src="x" onerror="alert()">');
      expect(encoded).toContain('&lt;');
      expect(encoded).toContain('&gt;');
      expect(encoded).toContain('&quot;');
    });
  });

  describe('Length & Type Validation', () => {
    test('nên enforce max length cho product name', () => {
      const result = validateProduct({
        productName: 'a'.repeat(101),
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(false);
      expect(result.errors.productName).toContain('100');
    });

    test('nên reject invalid price type', () => {
      const result = validateProduct({
        productName: 'Product',
        price: 'not a number',
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(false);
    });

    test('nên reject invalid quantity type', () => {
      const result = validateProduct({
        productName: 'Product',
        price: 99.99,
        quantity: 'abc',
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(false);
    });
  });

  describe('SQL Injection Prevention', () => {
    test('nên reject SQL injection attempt ở name', () => {
      const result = validateProduct({
        productName: "'; DROP TABLE products; --",
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      // Should either reject hoặc escape
      expect(result).toBeDefined();
    });

    test('nên handle UNION SELECT injection', () => {
      const result = validateProduct({
        productName: "' UNION SELECT * FROM users--",
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result).toBeDefined();
    });
  });

  describe('Special Characters Handling', () => {
    test('nên allow tiếng Việt ở product name', () => {
      const result = validateProduct({
        productName: 'Sản phẩm Tiếng Việt',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(true);
    });

    test('nên allow Unicode characters', () => {
      const result = validateProduct({
        productName: '日本語製品',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(true);
    });

    test('nên allow common safe special chars', () => {
      const result = validateProduct({
        productName: 'Product (v2.0) - Premium',
        price: 99.99,
        quantity: 10,
        category: 'Electronics'
      });
      
      expect(result.isValid).toBe(true);
    });
  });

  describe('Frontend Display Safety', () => {
    test('nên display sanitized content safely', () => {
      const dangerousInput = '<script>alert(1)</script>Safe Text';
      const sanitized = sanitizeInput(dangerousInput);
      
      // Should remove script but keep safe text
      expect(sanitized).not.toContain('<script>');
      expect(sanitized).toContain('Safe Text');
    });

    test('nên not render HTML from user input', () => {
      const input = '<b>bold</b>';
      const sanitized = sanitizeInput(input);
      
      // Should be escaped, not HTML
      expect(sanitized).not.toContain('<b>');
    });
  });
});
```

**Helper Functions** (tạo nếu chưa có):
```javascript
// frontend/src/utils/sanitization.js

/**
 * Escape HTML special characters
 */
export const escapeHTML = (text) => {
  if (typeof text !== 'string') return text;
  
  const map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#x27;',
  };
  
  return text.replace(/[&<>"']/g, (char) => map[char]);
};

/**
 * Sanitize user input - remove dangerous HTML
 */
export const sanitizeInput = (input) => {
  if (typeof input !== 'string') return input;
  
  // Remove script tags
  let sanitized = input.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
  
  // Remove event handlers
  sanitized = sanitized.replace(/on\w+\s*=\s*"[^"]*"/gi, '');
  sanitized = sanitized.replace(/on\w+\s*=\s*'[^']*'/gi, '');
  
  // Escape HTML
  sanitized = escapeHTML(sanitized);
  
  return sanitized;
};

/**
 * Validate product input with security checks
 */
export const validateProductSecure = (product) => {
  const errors = {};
  
  // Name validation
  if (!product.productName) {
    errors.productName = 'Tên không được để trống';
  } else if (product.productName.length < 3) {
    errors.productName = 'Tên phải ≥ 3 ký tự';
  } else if (product.productName.length > 100) {
    errors.productName = 'Tên ≤ 100 ký tự';
  } else if (/<|>/g.test(product.productName) || /javascript:/i.test(product.productName)) {
    errors.productName = 'Tên chứa ký tự không hợp lệ';
  }
  
  // Price validation
  if (!product.price || isNaN(product.price) || parseFloat(product.price) <= 0) {
    errors.price = 'Giá phải > 0';
  }
  
  // Quantity validation
  if (product.quantity === '' || isNaN(product.quantity) || parseInt(product.quantity) < 0) {
    errors.quantity = 'Số lượng không âm';
  }
  
  // Category validation
  const validCategories = ['Electronics', 'Books', 'Clothing', 'Toys', 'Groceries'];
  if (!product.category || !validCategories.includes(product.category)) {
    errors.category = 'Category không hợp lệ';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};
```

---

## 4. Cấu Trúc Code Project (Overview)

### Frontend Product Component:
```
frontend/
├── src/
│   ├── components/
│   │   ├── ProductManagement.jsx      ← Component Đức test
│   │   └── ProductManagement.css
│   ├── utils/
│   │   ├── validateProduct.js         ← Validation functions
│   │   └── sanitization.js            ← Security helpers (new)
│   ├── services/
│   │   └── productService.js
│   └── tests/
│       ├── ProductComponentsIntegration.test.jsx  ← ✓ ĐỨC LÀM
│       └── security/
│           └── sanitization.test.js   ← ✓ ĐỨC LÀM (BONUS)
```

### Backend Product API:
```
backend/
├── src/main/java/com/flogin/
│   ├── controller/
│   │   └── ProductController.java
│   ├── service/
│   │   └── ProductService.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── entity/
│       └── Product.java
```

### Performance Testing:
```
performance-tests/
├── scripts/
│   ├── product-test.js     ← ✓ ĐỨC LÀM (BONUS)
│   └── login-test.js
├── results/
│   └── stressTest.text
└── config.js
```

---

## 5. Giải Thích Ngắn Gọn Từng Loại Test

| Loại | Giải thích | Ví dụ |
|------|------------|-------|
| **Scenario Analysis** | Liệt kê từng tình huống người dùng | Create product → Show success |
| **Component Integration** | Test component + mocked API | Render ProductManagement, click Add |
| **Performance** | Test API dưới tải cao | 100 users tạo product, measure time |
| **Security** | Test xác thực input, ngăn XSS | Nhập `<script>` → reject |

---

## 6. Những Câu Hỏi Có Thể Bị Hỏi & Câu Trả Lời Mẫu

**Q1: "Test scenarios phục vụ gì?"**  
A: "Xác định tất cả tình huống người dùng mà hệ thống cần hỗ trợ. Từ đó đảm bảo code có đủ test cases cho toàn bộ requirement."

**Q2: "Component integration test khác gì so với unit test?"**  
A: "Unit test chỉ kiểm tra 1 hàm đơn lẻ, component integration test chạy toàn bộ component, tương tác form + API mock."

**Q3: "Performance test với 1000 users là gì?"**  
A: "Giả lập 1000 người dùng đồng thời gửi request tới API, đo response time và error rate để tìm breaking point."

**Q4: "Làm sao detect XSS?"**  
A: "Nhập `<script>alert(1)</script>` vào input, nếu script chạy thì bị XSS, kỳ vọng là script bị reject hoặc escape."

**Q5: "Sanitization là gì?"**  
A: "Quá trình làm sạch user input bằng cách remove dangerous tags (`<script>`, event handlers) hoặc escape special characters."

**Q6: "K6 hay JMeter, chọn cái nào?"**  
A: "K6 modern hơn, dùng JavaScript, dễ tích hợp CI/CD. JMeter GUI-based, tốt cho heavy load. Đề yêu cầu k6 hoặc JMeter cả 2 đều được."

---

## 7. Workflow Thực Hiện Công Việc

### Phase 1: Phân Tích (3 ngày) - 10 điểm
1. [ ] Review ProductManagement component
2. [ ] Review ProductService API
3. [ ] Liệt kê tất cả scenarios (CRUD, search, filter, pagination, validation)
4. [ ] Viết test case matrix (40 test cases)
5. [ ] Tạo traceability matrix (requirement → test case)

### Phase 2: Component Integration (3-4 ngày) - 5 điểm
1. [ ] Review ProductComponentsIntegration.test.jsx (có sẵn)
2. [ ] Extend test cases cho rendering (~6 tests)
3. [ ] Extend test cases cho modal operations (~4 tests)
4. [ ] Extend test cases cho validation (~4 tests)
5. [ ] Extend test cases cho search/filter (~3 tests)
6. [ ] Extend test cases cho pagination + feedback (~3 tests)
7. [ ] Run tests, fix failures

### Phase 3: Performance Testing (2-3 ngày BONUS) - 5 điểm
1. [ ] Cài đặt k6 hoặc JMeter
2. [ ] Viết load test script (100, 500, 1000 users)
3. [ ] Viết stress test script (tìm breaking point)
4. [ ] Run tests, collect metrics
5. [ ] Analyze results, tạo report

### Phase 4: Security Testing (2-3 ngày BONUS) - 5 điểm
1. [ ] Tạo sanitization.test.js
2. [ ] Viết test cases cho XSS prevention
3. [ ] Viết test cases cho HTML encoding
4. [ ] Viết test cases cho SQL injection
5. [ ] Viết test cases cho length validation
6. [ ] Tạo helper functions (escapeHTML, sanitizeInput)
7. [ ] Run tests, verify coverage

### Phase 5: Documentation & Report (1-2 ngày)
1. [ ] Tạo test report
2. [ ] Tạo performance report
3. [ ] Tạo security report
4. [ ] Chuẩn bị demo slides

---

## 8. Key Technical Points Cần Nhớ

### For Test Scenarios:
- Prioritize: HIGH (happy path), MEDIUM (error case), LOW (edge case)
- Format: TC ID, Description, Input, Expected Result
- Coverage: CRUD, Search/Filter, Pagination, Validation, Error handling

### For Component Integration:
- Jest + React Testing Library
- Query by: role, text, placeholder, testid (preferred order)
- Wait for async: `waitFor()`, `findBy()`
- Mock: API service, không mock component logic

### For Performance:
- k6 stages: ramp-up, steady, ramp-down
- Metrics: response time (p95, p99), error rate, throughput
- Thresholds: set expectation (p95 < 800ms, error < 1%)

### For Security:
- XSS payloads: `<script>`, `onerror=`, `onclick=`, SVG injection
- Encoding: `<` → `&lt;`, `"` → `&quot;`, `&` → `&amp;`
- Input validation + output encoding = layered defense

---

## 9. Checklist Tự Tin Trước Buổi Báo Cáo

- [ ] Nhớ tất cả 7 scenario sets cho Product (CRUD, search, filter, pagination, validation, error)
- [ ] Nhớ 20+ test cases cụ thể
- [ ] Có ví dụ component integration test
- [ ] Biết dùng cy.get(), cy.type(), cy.click(), cy.should()
- [ ] Hiểu k6 stages + thresholds
- [ ] Nhớ 4 XSS payloads: script tag, event handler, img onerror, SVG script
- [ ] Biết HTML encoding rules (< > " & ')
- [ ] Có roadmap mở rộng (accessibility, data-driven tests, etc.)

---

## 10. Files Đức Sẽ Tạo/Edit

| File | Status | Lines | Điểm |
|------|--------|-------|------|
| `docs/TEST_CASE_MATRIX_PRODUCT.md` | Create/Extend | ~300 | 10 |
| `frontend/src/tests/ProductComponentsIntegration.test.jsx` | Extend | +150 | 5 |
| `performance-tests/scripts/product-test.js` | Extend | +100 | 5 (bonus) |
| `frontend/src/utils/sanitization.js` | Create | ~100 | - (support) |
| `frontend/src/tests/security/sanitization.test.js` | Create | ~200 | 5 (bonus) |

**Tổng điểm: 15 + 10 (bonus)**

---

## 11. Tài Liệu Tham Khảo

**Frontend:**
- ProductManagement.jsx - Component structure
- ProductService.js - API methods
- validateProduct.js - Validation functions

**Testing:**
- jest - Unit test framework
- @testing-library/react - Component testing
- k6 - Performance testing tool
- cypress - E2E testing (Danh dùng)

**Security:**
- OWASP Top 10
- XSS Prevention Cheat Sheet
- Input Validation Best Practices

---

## 12. Demo Nhanh (5 Phút)

1. (2 phút) Mở `TEST_CASE_MATRIX_PRODUCT.md` → chỉ ra 7 scenario sets + traceability
2. (1.5 phút) Mở `ProductComponentsIntegration.test.jsx` → show integration test examples
3. (1 phút) Mở `product-test.js` k6 script → show load test results
4. (0.5 phút) Mention security tests (XSS payload examples)

---

## 13. Câu Hỏi Nâng Cao & Câu Trả Lời

| Câu hỏi | Câu trả lời |
|--------|------------|
| "Làm sao biết 40 test cases là đủ?" | Traceability matrix: mỗi requirement có ≥ 1 test case, cover happy + error + edge |
| "Performance test failure rate 10% có OK?" | Tuỳ SLA: nếu yêu cầu < 1% thì fail, nếu < 10% then OK |
| "Làm sao test XSS khi browser auto-escape?" | Kiểm tra response JSON + DOM content, không chỉ visual |
| "Component test phải mock API?" | Có, nếu không sẽ chậm và phụ thuộc backend. Mock → nhanh + independent |
| "K6 script chạy lâu bao lâu?" | Load test ~5-10 phút, stress test ~20-30 phút tùy stages |

---

## 14. Risk & Mitigation

| Rủi ro | Giảm thiểu |
|--------|------------|
| Test case thiếu → chất lượng thấp | Dùng traceability matrix, cover tất cả paths |
| Component test flaky | Mock API, tăng timeout, kiểm tra async |
| Performance test chậm CI/CD | Chỉ chạy load test, không chạy stress test ở CI |
| Security test bypass rule check | Review code, test cases cụ thể |

---

## 15. Timeline & Deadline

- **Ngày 1-2**: Phân tích, tạo test matrix (10 điểm)
- **Ngày 3-4**: Extend component integration tests (5 điểm)
- **Ngày 5-7**: Viết k6 performance tests (bonus 5 điểm)
- **Ngày 8-9**: Viết security sanitization tests (bonus 5 điểm)
- **Ngày 10**: Run tất cả tests, fix bugs
- **Ngày 11-12**: Chuẩn bị báo cáo, demo

---

## 16. Liên Quan Với Những Người Khác

- **Danh**: Backend Product tests, E2E Product, XSS testing (Backend perspective)
- **Nghĩa**: Product unit tests, SQL injection testing
- **Thành**: Login tests, authentication testing
- **Huy**: Login E2E, validation testing
- **Phát**: Backend API integration, CI/CD

---

## 17. Q&A Nhanh

**Q: "Tôi viết test scenarios nhưng không biết cách tạo test case ID?"**  
A: Dùng format: `SC[Scenario_Number].[TestCase_Number]`. Ví dụ SC1.1, SC1.2, ..., SC2.1, etc.

**Q: "Làm sao mock API trong component test?"**  
A: Dùng jest.mock() hoặc userEvent, mock productService.getAllProducts() trả về data cố định.

**Q: "Performance test fail vì timeout, làm gì?"**  
A: Kiểm tra API có caching chưa, database index có không, hoặc tăng timeout threshold.

**Q: "Security test cần thực hiện trên production?"**  
A: Không, test trên staging hoặc local dev environment. Production tests cần approval.

---

## 18. Ghi Chú Thêm

- ProductManagement component khá phức tạp, có modal + form + table + search + filter
- Backend API support pagination (page, size parameters)
- Có category dropdown với 5 options (Electronics, Books, Clothing, Toys, Groceries)
- K6 có built-in HTML report generation, dễ share results
- XSS testing quan trọng vì product name + description từ user input

---

Nếu có câu hỏi cụ thể về từng test case, hãy liên hệ mentor hoặc group.
