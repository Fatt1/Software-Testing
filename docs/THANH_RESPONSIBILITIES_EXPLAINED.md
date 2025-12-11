# Tổng Hợp Nhiệm Vụ Được Giao (Thành)

File này giúp Thành trả lời miệng tự tin: mình làm gì, ở lớp test nào, vì sao làm như vậy, và nếu bị hỏi sâu thì có câu trả lời ngắn gọn rõ ràng.

## 1. Phạm Vi Chính Thành Phụ Trách

### 1.1 Frontend Unit Tests - Login Validation (Câu 2.1) - **5 điểm**
- **File chính**: `frontend/src/tests/validation.test.js`
- **Mục tiêu**: Test các hàm validation được dùng trong LoginForm
  - `validateUsername()` - kiểm tra username hợp lệ
  - `validatePassword()` - kiểm tra password hợp lệ
  - `getPasswordStrength()` - tính độ mạnh mật khẩu

### 1.2 Frontend Unit Tests - Product Validation (Câu 3.1) - **5 điểm**
- **File chính**: `frontend/src/tests/validateProduct.test.js` hoặc tương tự
- **Mục tiêu**: Test hàm validation cho ProductManagement
  - `validateProduct()` - kiểm tra tên sản phẩm, giá, số lượng, category
  - Boundary tests: tên 2 ký tự → lỗi, 3 ký tự → pass
  - Price validation: > 0 là hợp lệ
  - Quantity validation: ≥ 0 là hợp lệ
  - Category validation: phải thuộc danh sách cho phép

### 1.3 E2E Setup và Configuration (Câu 5.1) - **1 điểm**
- **File chính**: `frontend/src/tests/cypress/` 
- **Mục tiêu**: Thiết lập và cấu hình Cypress cho E2E testing Login
  - Cấu hình `cypress.config.js` (có sẵn)
  - Setup fixtures users (có sẵn `users.json`)
  - Setup support files (`e2e.js`)

### 1.4 E2E Test Scenarios cho Login (Câu 5.1) - **2.5 điểm**
- **File chính**: `frontend/src/tests/cypress/e2e/login-scenarios.cy.js`
- **Mục tiêu**: Viết kịch bản E2E kiểm tra toàn bộ luồng đăng nhập
  - Scenario 1: Đăng nhập thành công với thông tin hợp lệ
  - Scenario 2: Đăng nhập thất bại với password sai
  - Scenario 3: Đăng nhập thất bại với username không tồn tại
  - Scenario 4: Xác thực lỗi validation (username < 3 ký tự, password < 6 ký tự)
  - Scenario 5: Kiểm tra toggle hiển thị/ẩn password
  - Scenario 6: Kiểm tra focus input khi nhấn Enter (username → password)

### 1.5 Security Testing - Authentication Bypass (Bonus) - **5 điểm (bonus)**
- **Mục tiêu**: Test các cách bypass authentication
  - Thử submit form với field rỗng
  - Thử nhập SQL injection ở username
  - Thử gửi request không qua frontend (direct API)
  - Kiểm tra token/session handling sau login

## 2. Mapping Nhiệm Vụ → File Cụ Thể & Điểm

| Nhiệm vụ | Loại test | File chính | Điểm | Status |
|----------|-----------|------------|------|--------|
| Login Validation Unit | Unit | `validation.test.js` | 5 | Có sẵn, cần review |
| Product Validation Unit | Unit | `validateProduct.test.js` | 5 | Cần viết |
| E2E Setup & Config | Setup | `cypress/` | 1 | Có sẵn |
| E2E Login Scenarios | E2E | `login-scenarios.cy.js` | 2.5 | Cần viết |
| Auth Bypass Security | Security | (new file) | 5 | Bonus |
| **Tổng điểm chính** | | | **13.5** | |

## 3. Chi Tiết Từng Phần Công Việc

### 3.1 Frontend Unit Tests - Login Validation (5 điểm) - ALREADY EXISTS

**Tình trạng hiện tại**: File `validation.test.js` đã có sẵn rất đầy đủ (~400 dòng code).

**Cần làm**:
- Review lại toàn bộ test cases (đã có 50+ test cases)
- Chắc chắn coverage cho:
  - `validateUsername()` - Test rỗng, quá ngắn (< 3), quá dài (> 50), ký tự đặc biệt
  - `validatePassword()` - Test rỗng, quá ngắn (< 6), quá dài (> 100), không có chữ, không có số
  - `getPasswordStrength()` - Test score 0-4, label, màu sắc

**Ví dụ test cases**:
```javascript
test('nên trả về không hợp lệ khi username là chuỗi rỗng', () => {
  const result = validateUsername("");
  expect(result.isValid).toBe(false);
  expect(result.error).toBe("Username không được để trống");
});

test('nên hợp lệ với đúng 3 ký tự', () => {
  const result = validateUsername("abc");
  expect(result.isValid).toBe(true);
  expect(result.error).toBeNull();
});
```

**Boundary cases**:
- Username: 2 (fail), 3 (pass), 50 (pass), 51 (fail)
- Password: 5 (fail), 6 (pass), 100 (pass), 101 (fail)
- Ký tự hợp lệ: `a-zA-Z0-9._-` cho username, chữ + số cho password

---

### 3.2 Frontend Unit Tests - Product Validation (5 điểm) - NEED TO WRITE

**Mục tiêu**: Kiểm tra tất cả validation rules cho Product form.

**Hàm cần test** (xem trong `ProductManagement.jsx` dòng 83-120):
```javascript
validateForm() {
  // Name: 3-100 ký tự
  // Price: > 0
  // Quantity: >= 0
  // Category: phải có giá trị
  // Description: optional nhưng nếu có thì <= 500 ký tự
}
```

**Test cases cần viết**:

| Test | Input | Expected |
|------|-------|----------|
| Product name rỗng | `""` | Error: "không được để trống" |
| Product name < 3 ký tự | `"ab"` | Error: "ít nhất 3 ký tự" |
| Product name = 3 ký tự | `"abc"` | Valid ✓ |
| Product name > 100 ký tự | 101 chars | Error: "không vượt quá 100" |
| Price = 0 | `0` | Error: "phải > 0" |
| Price < 0 | `-10` | Error: "phải > 0" |
| Price > 0 | `99.99` | Valid ✓ |
| Quantity < 0 | `-5` | Error: "không âm" |
| Quantity = 0 | `0` | Valid ✓ |
| Category rỗng | `""` | Error: "bắt buộc chọn" |
| Category hợp lệ | `"Electronics"` | Valid ✓ |
| Description > 500 | 501 chars | Error: "không vượt quá 500" |

**File structure**:
```
frontend/src/tests/validateProduct.test.js
├── describe('validateProduct() - Product Name')
│   ├── test: name rỗng
│   ├── test: name < 3 ký tự
│   ├── test: name = 3, 100 ký tự
│   └── test: name > 100 ký tự
├── describe('validateProduct() - Price')
│   ├── test: price ≤ 0
│   └── test: price > 0
├── describe('validateProduct() - Quantity')
│   ├── test: quantity < 0
│   └── test: quantity ≥ 0
├── describe('validateProduct() - Category')
│   ├── test: category rỗng
│   └── test: category hợp lệ
└── describe('validateProduct() - Description')
    ├── test: description > 500
    └── test: description ≤ 500
```

---

### 3.3 E2E Setup & Configuration (1 điểm) - ALREADY EXISTS

**Tình trạng**: Cấu hình Cypress đã có sẵn.

**Cần review**:
- `cypress.config.js` - baseUrl, timeouts
- `cypress/support/e2e.js` - custom commands
- `cypress/fixtures/users.json` - test data cho login

**Cypress command example** (đã có sẵn):
```javascript
// cypress/support/e2e.js
Cypress.Commands.add('login', (username, password) => {
  cy.visit('/');
  cy.get('input[type="text"]').type(username);
  cy.get('input[type="password"]').type(password);
  cy.get('button').contains('Đăng Nhập').click();
});
```

---

### 3.4 E2E Test Scenarios cho Login (2.5 điểm) - NEED TO WRITE

**File**: `frontend/src/tests/cypress/e2e/login-scenarios.cy.js`

**Scenario 1: Successful Login** (0.5 điểm)
```javascript
describe('Login E2E - Scenario 1: Successful Login', () => {
  it('nên đăng nhập thành công với thông tin hợp lệ', () => {
    cy.visit('/');
    cy.get('input[placeholder="your_username"]').type('testuser');
    cy.get('input[type="password"]').type('Password123');
    cy.get('button').contains('Đăng Nhập').click();
    
    // Verify success message appears
    cy.get('div').contains('Đăng nhập thành công').should('be.visible');
    
    // Verify redirect to products page after 2 seconds
    cy.url().should('include', '/products');
  });
});
```

**Scenario 2: Failed Login - Wrong Password** (0.5 điểm)
```javascript
describe('Login E2E - Scenario 2: Wrong Password', () => {
  it('nên hiển thị lỗi khi password sai', () => {
    cy.visit('/');
    cy.get('input[placeholder="your_username"]').type('testuser');
    cy.get('input[type="password"]').type('WrongPassword123');
    cy.get('button').contains('Đăng Nhập').click();
    
    // Verify error message
    cy.get('div').contains('Đăng nhập thất bại').should('be.visible');
    
    // User should stay on login page
    cy.url().should('include', '/');
  });
});
```

**Scenario 3: Validation Error - Username Too Short** (0.5 điểm)
```javascript
describe('Login E2E - Scenario 3: Validation Errors', () => {
  it('nên hiển thị lỗi khi username < 3 ký tự', () => {
    cy.visit('/');
    cy.get('input[placeholder="your_username"]').type('ab');
    cy.get('input[type="password"]').type('Password123');
    cy.get('button').contains('Đăng Nhập').click();
    
    // Verify field error message
    cy.get('p').contains('Username phải ít nhất 3 ký tự').should('be.visible');
    cy.get('button').contains('Đăng Nhập').should('be.disabled');
  });
});
```

**Scenario 4: Password Visibility Toggle** (0.5 điểm)
```javascript
describe('Login E2E - Scenario 4: Password Visibility', () => {
  it('nên toggle hiển thị/ẩn password', () => {
    cy.visit('/');
    const passwordInput = cy.get('input[type="password"]');
    const toggleBtn = cy.get('button').contains('👁️');
    
    // Initially password is hidden
    passwordInput.should('have.attr', 'type', 'password');
    
    // Click toggle to show password
    toggleBtn.click();
    passwordInput.should('have.attr', 'type', 'text');
    
    // Click toggle again to hide
    toggleBtn.click();
    passwordInput.should('have.attr', 'type', 'password');
  });
});
```

**Scenario 5: Keyboard Navigation - Enter Key** (0.5 điểm)
```javascript
describe('Login E2E - Scenario 5: Keyboard Navigation', () => {
  it('nên focus password field khi nhấn Enter ở username field', () => {
    cy.visit('/');
    const usernameInput = cy.get('input[placeholder="your_username"]');
    const passwordInput = cy.get('input[type="password"]');
    
    usernameInput.type('testuser{enter}');
    passwordInput.should('have.focus');
  });
  
  it('nên submit form khi nhấn Enter ở password field', () => {
    cy.visit('/');
    cy.get('input[placeholder="your_username"]').type('testuser');
    cy.get('input[type="password"]').type('Password123{enter}');
    
    // Should trigger login submission
    cy.get('button').contains('Đang xử lý').should('be.visible');
  });
});
```

---

### 3.5 Security Testing - Authentication Bypass (5 điểm BONUS)

**Mục tiêu**: Kiểm tra các cách bypass authentication khác nhau.

**Test Cases**:

| Test | Payload | Expected Result |
|------|---------|-----------------|
| Bypass - Empty Fields | `username="", password=""` | Show validation error, không gửi API |
| Bypass - SQL Injection | `username="' OR '1'='1"` | Rejected by backend validation |
| Bypass - Missing Token | Gửi direct API mà không có token | 401 Unauthorized |
| Bypass - Invalid Token | Token sai/expired | 401 Unauthorized |
| Bypass - CSRF Attack | Submit từ origin khác | CSRF protection reject |
| Brute Force | Gửi 100+ requests liên tục | Rate limiting kick in (nếu có) |

**Ví dụ test**:
```javascript
describe('Security - Authentication Bypass', () => {
  it('nên reject SQL injection ở username', () => {
    cy.visit('/');
    cy.get('input[placeholder="your_username"]').type("' OR '1'='1");
    cy.get('input[type="password"]').type('anypassword');
    cy.get('button').contains('Đăng Nhập').click();
    
    // Should show validation error or 401
    cy.get('div').contains(/không hợp lệ|thất bại/).should('be.visible');
  });
});
```

---

## 4. Cấu Trúc Code Project (Overview)

### Frontend Login Component Structure:
```
frontend/
├── src/
│   ├── components/
│   │   ├── LoginForm.jsx           ← Component test ở LoginIntegration.test.jsx
│   │   └── LoginForm.css
│   ├── utils/
│   │   └── vailidation.js          ← Validation functions (đã có test)
│   ├── services/
│   │   └── authService.js          ← API service (mocked trong tests)
│   └── tests/
│       ├── validation.test.js      ← ✓ THÀNH LÀM (Unit Tests for Login)
│       ├── validateProduct.test.js ← ✓ THÀNH LÀM (Unit Tests for Product)
│       ├── LoginIntegration.test.jsx
│       └── cypress/
│           ├── e2e/
│           │   └── login-scenarios.cy.js  ← ✓ THÀNH LÀM (E2E Login)
│           ├── fixtures/
│           │   └── users.json
│           ├── pages/
│           │   └── LoginPage.js   (Page Object Model)
│           └── support/
│               └── e2e.js
```

### Frontend Product Component Structure:
```
frontend/
├── src/
│   ├── components/
│   │   ├── ProductManagement.jsx   ← Component cần validate
│   │   └── ProductManagement.css
│   ├── utils/
│   │   ├── vailidation.js
│   │   └── validateProduct.js      ← Validation functions cần test
│   ├── services/
│   │   └── productService.js
│   └── tests/
│       ├── validateProduct.test.js ← ✓ THÀNH LÀM
│       ├── ProductComponentsIntegration.test.jsx
│       └── cypress/
│           ├── e2e/
│           │   ├── product-management.cy.js
│           │   └── product-e2e-scenarios.cy.js
│           ├── pages/
│           │   └── ProductPage.js
│           └── fixtures/
│               └── products.json
```

---

## 5. Giải Thích Ngắn Gọn Từng Loại Test

| Loại | Giải thích | Ví dụ |
|------|------------|-------|
| **Unit** | Kiểm tra hàm đơn lẻ, không cần chạy server | `validateUsername("abc")` → true |
| **Integration** | Test component + mocked service | Render LoginForm + mock authService |
| **E2E** | Chạy trình duyệt thực tế, người dùng thực | Visit `/`, type username, click login |
| **Security** | Kiểm tra lỗ hổng (SQL injection, XSS, CSRF) | Nhập `' OR '1'='1` vào username |

---

## 6. Những Câu Hỏi Có Thể Bị Hỏi & Câu Trả Lời Mẫu

**Q1: "Khác nhau giữa validation.test.js và validateProduct.test.js là gì?"**  
A: "validation.test.js là unit test cho hàm validateUsername và validatePassword dùng trong Login. validateProduct.test.js là unit test cho hàm validateProduct dùng trong Product Management. Cả hai đều kiểm tra business rules của validation."

**Q2: "E2E test Login Scenarios khác gì so với unit test?"**  
A: "Unit test chỉ kiểm tra hàm validateUsername() với input, output nhất định. E2E test chạy trình duyệt thực tế, người dùng nhập vào form, click nút, và xem UI thay đổi. E2E đầy đủ hơn nhưng chậm hơn."

**Q3: "Boundary test là gì?"**  
A: "Kiểm tra giá trị ranh giới. Ví dụ: username phải 3-50 ký tự, nên test với 2 ký tự (fail), 3 ký tự (pass), 50 ký tự (pass), 51 ký tự (fail)."

**Q4: "Làm sao test password strength?"**  
A: "Score 0-4 dựa vào: độ dài (6, 8, 12+), variety (chữ hoa/thường/số/special chars). Ví dụ 'abc123' = score 1, 'Pass@123' = score 4."

**Q5: "Security bypass test là gì?"**  
A: "Cố gắng đăng nhập mà không cần password đúng, ví dụ dùng SQL injection hoặc gửi request không qua frontend. Kỳ vọng: hệ thống bảo vệ, reject request."

**Q6: "Làm sao setup Cypress cho E2E?"**  
A: "Có sẵn cypress.config.js, chỉ cần viết test cases ở login-scenarios.cy.js, dùng cy.visit(), cy.get(), cy.type(), cy.click() để tương tác."

---

## 7. Workflow Thực Hiện Công Việc

### Phase 1: Review & Understand (1 ngày)
1. [ ] Đọc toàn bộ `validation.test.js` - đã có sẵn
2. [ ] Hiểu structure LoginForm.jsx - input, validation flow
3. [ ] Hiểu structure ProductManagement.jsx - form validation
4. [ ] Review cypress.config.js + cypress structure

### Phase 2: Write Tests (3-4 ngày)
1. [ ] Viết `validateProduct.test.js` (~150 lines)
   - Setup describe blocks
   - Test boundary cases
   - Test negative cases
   
2. [ ] Viết `login-scenarios.cy.js` (~200 lines)
   - 5 scenarios (mỗi ~40 lines)
   - Setup Page Object nếu cần
   - Test selectors chính xác

3. [ ] Viết security tests (bonus) (~100 lines)
   - SQL injection, XSS, CSRF scenarios
   - Brute force test

### Phase 3: Run & Fix (1-2 ngày)
1. [ ] Run `npm test` cho unit tests
2. [ ] Run `npm run test:e2e` cho Cypress
3. [ ] Fix failing tests
4. [ ] Verify coverage

---

## 8. Key Technical Points Cần Nhớ

### For Unit Tests:
- Jest syntax: `describe()`, `test()`, `expect()`
- Setup: import hàm validation, không cần component
- Mock: không cần mock, test pure functions
- Assertions: `.toBe()`, `.toBeNull()`, `.toMatch()`

### For E2E Tests:
- Cypress syntax: `cy.visit()`, `cy.get()`, `cy.type()`, `cy.click()`
- Selectors: dùng placeholder, text content, hoặc data-testid
- Waits: `cy.should('be.visible')`, `cy.url().should('include')`
- Fixtures: `users.json` có user test data sẵn

### For Security Tests:
- SQL Injection: `' OR '1'='1`, `admin'--`, `1'; DROP TABLE--`
- XSS: `<script>alert(1)</script>`, `" onmouseover="alert(1)`
- CSRF: check X-CSRF-Token header
- Brute force: gửi 100+ requests, check rate limiting

---

## 9. Checklist Tự Tin Trước Buổi Báo Cáo

- [ ] Nhớ 3 loại test: Unit, Integration, E2E
- [ ] Nhớ validation rules: username 3-50, password 6-100, product name 3-100
- [ ] Có ví dụ boundary test: name "ab" → fail, name "abc" → pass
- [ ] Biết dùng cy.visit(), cy.get(), cy.type(), cy.click()
- [ ] Hiểu Page Object Model (gom selectors)
- [ ] Nêu được 3 test case cho each scenario
- [ ] Biết 3 loại security attack: SQL injection, XSS, CSRF

---

## 10. Files Thành Sẽ Tạo/Edit

| File | Status | Lines | Điểm |
|------|--------|-------|------|
| `frontend/src/tests/validation.test.js` | Review | ~400 | 5 |
| `frontend/src/tests/validateProduct.test.js` | Create | ~150 | 5 |
| `frontend/src/tests/cypress/e2e/login-scenarios.cy.js` | Create | ~200 | 2.5 |
| `frontend/src/tests/cypress/support/e2e.js` | Review | ~50 | 1 |
| `frontend/src/tests/cypress/security-tests.cy.js` | Create (bonus) | ~100 | 5 |

**Tổng điểm: 13.5 + 5 (bonus)**

---

## 11. Tài Liệu Tham Khảo

**Frontend:**
- LoginForm.jsx - Component structure (passwordfield, validation)
- ProductManagement.jsx - Product form validation
- vailidation.js - Validation functions
- validateProduct.js - Product validation

**Testing:**
- `jest` - Unit test framework
- `@testing-library/react` - React component testing
- `cypress` - E2E testing framework

**Packages trong `package.json`**:
- jest, babel-jest
- @testing-library/react, @testing-library/user-event
- cypress

---

## 12. Demo Nhanh (3 Phút)

1. (1 phút) Mở `validation.test.js` → chỉ ra boundary test (3 ký tự pass, 2 fail)
2. (1 phút) Mở `login-scenarios.cy.js` → chỉ scenario 1: successful login
3. (1 phút) Chạy `npm test` → show passing tests

---

## 13. Câu Hỏi Nâng Cao & Câu Trả Lời

| Câu hỏi | Câu trả lời |
|--------|------------|
| "Làm sao test async code trong Cypress?" | `cy.intercept()` để mock API, `cy.wait()` để chờ |
| "Làm sao retry failed assertion?" | Cypress tự retry 4s mặc định, không cần code thêm |
| "Làm sao test localStorage/session?" | `cy.window().then(win => win.localStorage)` |
| "Làm sao organize test cases?" | Dùng `describe()` lồng nhau theo feature/scenario |
| "Performance của E2E test?" | 1 test ~5-10s, nên chia nhỏ scenario |

---

## 14. Risk & Mitigation

| Rủi ro | Giảm thiểu |
|--------|------------|
| Selector thay đổi → E2E fail | Dùng data-testid hoặc Page Object Model |
| Test quá lâu → slow CI/CD | Chỉ test happy path + 1 error case |
| Flaky test (some pass, some fail) | Tăng timeout, kiểm tra async code |
| Không hiểu validation rules | Document rõ ràng, có test cases comment |

---

## 15. Tìm Kiếm & Gỡ Lỗi

**Nếu unit test fail**:
```bash
npm test -- validation.test.js --verbose
```

**Nếu E2E test fail**:
```bash
npm run test:e2e -- --headed  # Mở trình duyệt để debug
```

**Check selectors**:
- Devtools F12 → inspect element → lấy placeholder/text/data-testid

---

## 16. Timeline & Deadline

- **Ngày 1-2**: Review, hiểu structure (validation.test.js exists, chỉ review)
- **Ngày 3-5**: Viết validateProduct.test.js (~150 lines)
- **Ngày 5-7**: Viết login-scenarios.cy.js (~200 lines)
- **Ngày 8**: Run tests, fix bugs
- **Ngày 9**: Bonus security tests (nếu còn thời gian)
- **Ngày 10**: Báo cáo, giải thích chi tiết

---

## 17. Liên Quan Với Những Người Khác

- **Phát**: Backend unit tests (LoginService), CI/CD integration
- **Huy**: Phân tích test scenarios, validation test
- **Nghĩa**: Product unit tests, CI/CD, security testing
- **Danh**: Product backend tests, E2E Product, XSS testing
- **Đức**: Performance testing (k6/JMeter), security sanitization

---

## 18. Q&A Nhanh

**Q: "Tôi viết xong test nhưng nó fail, làm gì?"**  
A: Kiểm tra selector (placeholder, id, text) có đúng không? Dùng `cy.pause()` để debug.

**Q: "Làm sao biết test đủ không?"**  
A: Coverage >= 80%, test tất cả validation rules, test boundary & negative cases.

**Q: "Có cần test UI styling không?"**  
A: Không, chỉ test functionality (input, button click, error message).

**Q: "Có cần test password visib toggle?"**  
A: Có, vì đó là feature quan trọng (scenario 4).

---

## 19. Ghi Chú Thêm

- LoginForm.jsx đã khá hoàn chỉnh với validation display
- ProductManagement.jsx có khá nhiều validation rules
- Cypress setup sẵn, chỉ cần viết test cases
- Page Object Model optional nhưng giúp maintain dễ hơn

---

Nếu có câu hỏi cụ thể về từng test case, hãy liên hệ mentor hoặc group.
