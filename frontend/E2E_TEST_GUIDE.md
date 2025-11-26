# 🧪 Hướng Dẫn Chạy E2E Automation Test với Cypress

## 📋 Yêu Cầu

- **Node.js**: v16.0.0 hoặc cao hơn
- **npm**: v7.0.0 hoặc cao hơn
- **Cypress**: Đã được cài đặt

## 📦 Cài Đặt

### 1. Cài đặt Cypress (nếu chưa có)
```bash
cd frontend
npm install --save-dev cypress
```

## 🚀 Chạy Automation Test

### Cách 1: Chạy Test ở Chế độ Headless (CLI)

**Bước 1**: Mở terminal đầu tiên - chạy dev server
```bash
cd frontend
npm run dev
```
Dev server sẽ chạy ở `http://localhost:5173` hoặc `http://localhost:5174`

**Bước 2**: Mở terminal thứ 2 - chạy E2E test
```bash
cd frontend
npm run e2e
```

### Cách 2: Chạy Test ở Chế độ Interactive (Cypress UI)

**Bước 1**: Mở terminal đầu tiên - chạy dev server
```bash
cd frontend
npm run dev
```

**Bước 2**: Mở terminal thứ 2 - mở Cypress UI
```bash
cd frontend
npm run e2e:open
```

Cypress sẽ mở giao diện cho phép bạn:
- Xem từng test chạy
- Debug lỗi
- Xem chi tiết từng step

## 📊 Cấu Trúc Test

### Test Files
- **`cypress/e2e/login.cy.js`** - Login tests với Page Object Model
- **`cypress/e2e/login-scenarios.cy.js`** - Các scenario đăng nhập khác nhau

### Test Data
- **`cypress/fixtures/users.json`** - User test data

### Page Objects
- **`cypress/pages/LoginPage.js`** - Page Object cho Login page

## 🔧 Cấu Hình

### Cypress Config (`cypress.config.js`)
```javascript
baseUrl: 'http://localhost:5174'    // Địa chỉ app
viewportWidth: 1280                 // Chiều rộng viewport
viewportHeight: 720                 // Chiều cao viewport
defaultCommandTimeout: 10000        // Timeout mặc định
pageLoadTimeout: 30000              // Timeout load page
```

## 📝 Test Cases

### Setup và Configuration (10 điểm)
- ✅ Cypress được cài đặt và cấu hình
- ✅ Test environment được cấu hình đúng
- ✅ Page Object Model được thiết lập
- ✅ Test data fixtures được tải
- ✅ Login page load thành công
- ✅ Page selectors hoạt động đúng
- ✅ Custom commands được đăng ký
- ✅ Environment configuration đúng
- ✅ Browser viewport được cấu hình
- ✅ Local storage được clear

### Page Object Model Tests
- ✅ LoginPage có tất cả required selectors
- ✅ LoginPage có tất cả required methods
- ✅ Input email qua Page Object
- ✅ Input password qua Page Object
- ✅ Submit form qua Page Object
- ✅ Verify error message qua Page Object

### Form Validation Tests
- ✅ Email input có placeholder
- ✅ Password input có placeholder
- ✅ Nhập email và submit
- ✅ Login button enabled khi form filled

### Navigation Tests
- ✅ Forgot password link tồn tại
- ✅ Sign up link tồn tại
- ✅ Remember me checkbox tồn tại

### User Interaction Tests
- ✅ Nhập email từng ký tự
- ✅ Xoá email sau khi nhập
- ✅ Nhập password và submit
- ✅ Click multiple times

## 🐛 Troubleshooting

### Lỗi: "Connection refused"
**Giải pháp**: Chắc chắn dev server đang chạy ở port 5173 hoặc 5174
```bash
npm run dev
```

### Lỗi: "Command not found: npm"
**Giải pháp**: Cài đặt Node.js từ https://nodejs.org/

### Lỗi: "Cypress timeout"
**Giải pháp**: Tăng timeout trong `cypress.config.js`:
```javascript
defaultCommandTimeout: 20000
```

### Lỗi: "Page not found"
**Giải pháp**: Kiểm tra baseUrl trong `cypress.config.js` khớp với port dev server

## 📊 Kết Quả Test

Khi chạy test, bạn sẽ thấy:
- ✅ **Passing tests** - Test thành công
- ❌ **Failing tests** - Test thất bại (nếu có)
- ⏭️ **Skipped tests** - Test bị skip

### Screenshots
Nếu test thất bại, Cypress sẽ tạo screenshot tại:
```
cypress/screenshots/
```

## 🎯 Best Practices

1. **Luôn chạy dev server trước** khi chạy E2E test
2. **Dùng Page Object Model** để tổ chức selectors
3. **Xử lý explicit waits** cho các element động
4. **Dùng fixtures** cho test data
5. **Ghi log** cho debugging

## 📚 Tài Liệu

- [Cypress Documentation](https://docs.cypress.io)
- [Page Object Model Pattern](https://docs.cypress.io/guides/references/best-practices#Organizing-Tests)
- [Cypress Best Practices](https://docs.cypress.io/guides/references/best-practices)

---

**Để chạy test**, hãy mở 2 terminal:
1. Terminal 1: `npm run dev` (dev server)
2. Terminal 2: `npm run e2e` hoặc `npm run e2e:open` (test)

---

## 🔧 Advanced Cypress Techniques

### Custom Commands

**Creating Reusable Commands:**

```javascript
// cypress/support/commands.js

// Login command
Cypress.Commands.add('login', (username, password) => {
  cy.session([username, password], () => {
    cy.visit('/login');
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/products');
  });
});

// Create product command
Cypress.Commands.add('createProduct', (product) => {
  cy.get('button').contains('Add Product').click();
  cy.get('input[name="productName"]').type(product.name);
  cy.get('input[name="price"]').type(product.price);
  cy.get('input[name="quantity"]').type(product.quantity);
  cy.get('select[name="category"]').select(product.category);
  cy.get('textarea[name="description"]').type(product.description);
  cy.get('button[type="submit"]').click();
  cy.contains(/successfully added/i).should('be.visible');
});

// API request command
Cypress.Commands.add('apiLogin', (username, password) => {
  return cy.request({
    method: 'POST',
    url: 'http://localhost:8080/api/auth/login',
    body: { userName: username, password: password }
  }).then((response) => {
    window.localStorage.setItem('token', response.body.token);
    return response.body;
  });
});

// Wait for API command
Cypress.Commands.add('waitForApi', (alias, timeout = 10000) => {
  cy.wait(alias, { timeout });
});
```

**Usage:**
```javascript
describe('Product Management', () => {
  beforeEach(() => {
    cy.login('admin', 'admin123');  // Use custom login
    cy.visit('/products');
  });
  
  it('should create product using custom command', () => {
    cy.createProduct({
      name: 'Test Product',
      price: '1000000',
      quantity: '10',
      category: 'Điện tử',
      description: 'Test description'
    });
    
    cy.contains('Test Product').should('be.visible');
  });
});
```

### Intercepting Network Requests

**Mock API Responses:**
```javascript
describe('API Mocking', () => {
  it('should mock product list', () => {
    cy.intercept('GET', '/api/products*', {
      statusCode: 200,
      body: {
        content: [
          { id: 1, productName: 'Mocked Product', price: 1000000 }
        ],
        totalPages: 1,
        totalElements: 1
      }
    }).as('getProducts');
    
    cy.visit('/products');
    cy.wait('@getProducts');
    cy.contains('Mocked Product').should('be.visible');
  });
  
  it('should handle API errors', () => {
    cy.intercept('GET', '/api/products*', {
      statusCode: 500,
      body: { error: 'Internal Server Error' }
    }).as('getProductsError');
    
    cy.visit('/products');
    cy.wait('@getProductsError');
    cy.contains(/error loading/i).should('be.visible');
  });
  
  it('should delay API response', () => {
    cy.intercept('GET', '/api/products*', (req) => {
      req.reply((res) => {
        res.delay(2000);  // Simulate slow network
      });
    }).as('slowProducts');
    
    cy.visit('/products');
    cy.get('.loading-spinner').should('be.visible');
    cy.wait('@slowProducts');
    cy.get('.loading-spinner').should('not.exist');
  });
});
```

### Working with Local Storage & Cookies

**Local Storage:**
```javascript
it('should persist auth token in local storage', () => {
  cy.login('admin', 'admin123');
  
  cy.window().then((win) => {
    const token = win.localStorage.getItem('token');
    expect(token).to.exist;
    expect(token).to.be.a('string');
    expect(token.length).to.be.greaterThan(20);
  });
});

it('should clear local storage on logout', () => {
  cy.login('admin', 'admin123');
  cy.visit('/products');
  cy.get('button').contains('Logout').click();
  
  cy.window().then((win) => {
    expect(win.localStorage.getItem('token')).to.be.null;
  });
});
```

**Cookies:**
```javascript
it('should set session cookie', () => {
  cy.login('admin', 'admin123');
  cy.getCookie('sessionId').should('exist');
});

it('should clear cookies on logout', () => {
  cy.login('admin', 'admin123');
  cy.clearCookies();
  cy.getCookie('sessionId').should('not.exist');
});
```

### File Upload Testing

```javascript
it('should upload product image', () => {
  cy.get('input[type="file"]').attachFile('product-image.jpg');
  cy.get('img.preview').should('be.visible');
  cy.get('img.preview').should('have.attr', 'src').and('include', 'blob:');
});

it('should validate file size', () => {
  cy.get('input[type="file"]').attachFile('large-file.jpg');
  cy.contains('File size exceeds limit').should('be.visible');
});
```

### Drag and Drop Testing

```javascript
it('should reorder products via drag and drop', () => {
  cy.get('.product-row').first().as('firstProduct');
  cy.get('.product-row').last().as('lastProduct');
  
  cy.get('@firstProduct')
    .drag('@lastProduct');
  
  cy.get('.product-row').last().should('contain', 'Original First Product');
});
```

---

## 🐛 Debugging Techniques

### 1. Using cy.debug() and cy.pause()

```javascript
it('should debug test', () => {
  cy.visit('/products');
  cy.debug();  // Opens DevTools with current state
  
  cy.get('.product-row').first().click();
  cy.pause();  // Pauses test execution
  
  cy.get('.product-details').should('be.visible');
});
```

**Interactive Debugging:**
- Click "Resume" button to continue
- Inspect DOM state at pause point
- Modify selectors in DevTools console

### 2. Screenshot on Failure

```javascript
// cypress.config.js
module.exports = {
  screenshotOnRunFailure: true,
  video: true,
  screenshotsFolder: 'cypress/screenshots',
  videosFolder: 'cypress/videos'
};

// Take manual screenshot
it('should capture screenshot', () => {
  cy.visit('/products');
  cy.screenshot('products-page');
});
```

### 3. Logging and Assertions

```javascript
it('should log test progress', () => {
  cy.log('Starting product creation test');
  cy.visit('/products');
  
  cy.get('button').contains('Add Product')
    .should('be.visible')
    .then(($btn) => {
      cy.log('Button text:', $btn.text());
      cy.log('Button classes:', $btn.attr('class'));
    })
    .click();
  
  cy.log('Modal should be open');
  cy.get('.modal').should('be.visible');
});
```

### 4. Network Request Debugging

```javascript
it('should debug API calls', () => {
  cy.intercept('GET', '/api/products*', (req) => {
    console.log('Request:', req);
    req.continue((res) => {
      console.log('Response:', res);
    });
  }).as('getProducts');
  
  cy.visit('/products');
  cy.wait('@getProducts').then((interception) => {
    cy.log('Status:', interception.response.statusCode);
    cy.log('Body:', interception.response.body);
  });
});
```

---

## 📊 Test Organization Patterns

### 1. Grouping Related Tests

```javascript
describe('Product Management', () => {
  context('When user is authenticated', () => {
    beforeEach(() => {
      cy.login('admin', 'admin123');
      cy.visit('/products');
    });
    
    describe('Creating products', () => {
      it('should create product with valid data', () => {});
      it('should validate required fields', () => {});
      it('should show success message', () => {});
    });
    
    describe('Editing products', () => {
      it('should load existing product data', () => {});
      it('should update product', () => {});
      it('should show updated values', () => {});
    });
  });
  
  context('When user is not authenticated', () => {
    it('should redirect to login', () => {
      cy.visit('/products');
      cy.url().should('include', '/login');
    });
  });
});
```

### 2. Tagging Tests

```javascript
// Add tags using test names
describe('Product Tests', () => {
  it('[smoke] should load product page', () => {});
  it('[smoke] should display products', () => {});
  it('[regression] should filter by category', () => {});
  it('[regression] should handle pagination', () => {});
});

// Run specific tags
// npx cypress run --spec "**/*smoke*.spec.js"
```

### 3. Shared Test Data

```javascript
// cypress/fixtures/products.json
{
  "validProduct": {
    "name": "Test Laptop",
    "price": "25000000",
    "quantity": "10",
    "category": "Điện tử",
    "description": "High-performance laptop for testing"
  },
  "invalidProduct": {
    "name": "ab",
    "price": "-100",
    "quantity": "-5"
  }
}

// Usage in tests
it('should create product from fixture', () => {
  cy.fixture('products').then((products) => {
    cy.createProduct(products.validProduct);
    cy.contains(products.validProduct.name).should('be.visible');
  });
});
```

---

## ⚙️ Configuration Best Practices

### Environment Variables

```javascript
// cypress.config.js
module.exports = {
  env: {
    apiUrl: 'http://localhost:8080',
    adminUsername: 'admin',
    adminPassword: 'admin123'
  },
  e2e: {
    baseUrl: 'http://localhost:5173'
  }
};

// Usage in tests
it('should use env variables', () => {
  const apiUrl = Cypress.env('apiUrl');
  cy.request(`${apiUrl}/api/products`);
});
```

### Multiple Environments

```javascript
// cypress.env.json (local)
{
  "baseUrl": "http://localhost:5173",
  "apiUrl": "http://localhost:8080"
}

// cypress.staging.json
{
  "baseUrl": "https://staging.example.com",
  "apiUrl": "https://api-staging.example.com"
}

// Run with specific environment
// npx cypress run --env configFile=staging
```

### Retry Strategy

```javascript
// cypress.config.js
module.exports = {
  retries: {
    runMode: 2,      // Retry failed tests 2 times in CI
    openMode: 0      // Don't retry in interactive mode
  },
  e2e: {
    defaultCommandTimeout: 10000,
    pageLoadTimeout: 30000,
    requestTimeout: 10000
  }
};
```

---

## 🚀 Performance Optimization

### 1. Use cy.session() for Login

```javascript
Cypress.Commands.add('login', (username, password) => {
  cy.session(
    [username, password],
    () => {
      cy.visit('/login');
      cy.get('#username').type(username);
      cy.get('#password').type(password);
      cy.get('button[type="submit"]').click();
    },
    {
      validate() {
        cy.window().then((win) => {
          expect(win.localStorage.getItem('token')).to.exist;
        });
      }
    }
  );
});

// Session is cached across tests - faster execution!
```

### 2. Minimize Network Requests

```javascript
// Mock static data
beforeEach(() => {
  cy.intercept('GET', '/api/products', { fixture: 'products.json' });
  cy.intercept('GET', '/api/categories', { fixture: 'categories.json' });
});

// Real API only for critical paths
it('should actually create product', () => {
  cy.intercept('POST', '/api/products').as('createProduct');
  // ... test with real API
});
```

### 3. Reduce Unnecessary Waits

```javascript
// ❌ Bad - arbitrary wait
cy.wait(2000);
cy.get('.modal').should('be.visible');

// ✅ Good - wait for specific condition
cy.get('.modal').should('be.visible');

// ✅ Better - increase timeout if needed
cy.get('.modal', { timeout: 10000 }).should('be.visible');
```

---

## 🌐 Cross-Browser Testing

```javascript
// Run tests in multiple browsers
// npx cypress run --browser chrome
// npx cypress run --browser firefox
// npx cypress run --browser edge

// Browser-specific tests
it('should work in Chrome', { browser: 'chrome' }, () => {
  // Chrome-specific test
});

it('should work in Firefox', { browser: 'firefox' }, () => {
  // Firefox-specific test
});

// Skip tests in specific browsers
it('should skip in Firefox', { browser: '!firefox' }, () => {
  // Runs in all browsers except Firefox
});
```

---

## 📚 Real-World Examples

### Example 1: Complete User Journey

```javascript
describe('Complete Product Management Journey', () => {
  it('should complete full CRUD cycle', () => {
    // Login
    cy.login('admin', 'admin123');
    cy.visit('/products');
    
    // Create product
    const productName = `Test Product ${Date.now()}`;
    cy.createProduct({
      name: productName,
      price: '5000000',
      quantity: '20',
      category: 'Điện tử',
      description: 'Created via automated test'
    });
    
    // Search for product
    cy.get('input[placeholder*="Tìm kiếm"]').type(productName);
    cy.contains(productName).should('be.visible');
    
    // View details
    cy.contains(productName).click();
    cy.get('.product-details').within(() => {
      cy.contains('5000000').should('be.visible');
      cy.contains('20').should('be.visible');
    });
    
    // Edit product
    cy.get('button').contains('Edit').click();
    cy.get('input[name="price"]').clear().type('6000000');
    cy.get('button[type="submit"]').click();
    cy.contains(/updated successfully/i).should('be.visible');
    
    // Verify update
    cy.contains(productName).click();
    cy.contains('6000000').should('be.visible');
    
    // Delete product
    cy.get('button').contains('Delete').click();
    cy.get('.confirm-dialog').within(() => {
      cy.contains('Yes').click();
    });
    cy.contains(/deleted successfully/i).should('be.visible');
    
    // Verify deletion
    cy.get('input[placeholder*="Tìm kiếm"]').type(productName);
    cy.contains('No products found').should('be.visible');
  });
});
```

### Example 2: Error Handling

```javascript
describe('Error Scenarios', () => {
  beforeEach(() => {
    cy.login('admin', 'admin123');
    cy.visit('/products');
  });
  
  it('should handle duplicate product name', () => {
    cy.intercept('POST', '/api/products', {
      statusCode: 400,
      body: { error: 'Product name already exists' }
    }).as('createDuplicate');
    
    cy.createProduct({ name: 'Duplicate Product', /* ... */ });
    cy.wait('@createDuplicate');
    cy.contains('Product name already exists').should('be.visible');
  });
  
  it('should handle network timeout', () => {
    cy.intercept('GET', '/api/products', (req) => {
      req.destroy();  // Simulate network failure
    }).as('networkError');
    
    cy.visit('/products');
    cy.contains(/network error|failed to load/i).should('be.visible');
  });
});
```
