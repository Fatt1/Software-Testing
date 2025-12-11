# Phân Công Công Việc - Testing Project (Chi Tiết)

**Project**: Software Testing - Login & Product Management  
**Ngày phân công**: December 4, 2025  
**Thành viên**: Phát, Huy, Thành, Nghĩa, Danh, Đức

---

## I. TỔNG QUAN PHÂN CÔNG

### Login Module (Phát, Thành, Huy)
- **Phát**: Backend unit tests + CI/CD integration
- **Thành**: Frontend unit tests + E2E setup + configuration
- **Huy**: Test scenarios + validation + mock testing

### Product Module (Nghĩa, Danh, Đức)
- **Danh**: Backend unit + integration + E2E + XSS security
- **Nghĩa**: Frontend unit tests + SQL injection security
- **Đức**: Test scenarios + component integration + performance + sanitization

### Special Tasks
- **Nghĩa**: CI/CD integration cho Login tests (đã assign)
- **Phát**: CI/CD integration cho Product tests
- **Huy**: Input validation security testing
- **Đức**: Output sanitization security testing

---

## II. CHI TIẾT CÔNG VIỆC

### A. LOGIN MODULE

#### A.1 Câu 1.1: Login - Phân Tích và Test Scenarios (10 điểm) - **HUY**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Phân tích toàn bộ luồng login, viết test scenarios | 10 |
| **File output** | `docs/TEST_CASE_MATRIX_LOGIN.md` hoặc extend `TEST_CASE_MATRIX_BACKEND.md` | - |
| **Test cases** | Min 30 test cases (authentication, validation, error handling, edge cases) | - |
| **Deliverable** | Markdown doc với scenario matrix + traceability | - |

**Chi tiết Test Scenarios (30+ cases)**:
- Authentication Success/Failure (2 cases)
- Validation Errors - Username (4 cases: empty, short, long, special chars)
- Validation Errors - Password (4 cases: empty, short, no letter, no number)
- Error Handling (5 cases: 401, 403, 500, timeout, network error)
- Edge Cases (5 cases: empty fields, spaces only, SQL injection attempt, XSS attempt)
- Boundary Cases (4 cases: exact min/max lengths)
- Integration Cases (4 cases: password visibility, keyboard nav, remember me, forgot password)

---

#### A.2 Câu 2.1: Login - Unit Tests Frontend (5 điểm) - **THÀNH**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Unit test các hàm validation dùng trong Login | 5 |
| **File** | `frontend/src/tests/validation.test.js` (đã có, cần review) | - |
| **Test cases** | 50+ test cases (đã có sẵn) | - |
| **Coverage** | validateUsername(), validatePassword(), getPasswordStrength() | - |
| **Status** | Review existing, ensure all test cases pass | - |

**Hàm test**:
- `validateUsername()` - kiểm tra 3-50 ký tự, pattern [a-zA-Z0-9._-]
- `validatePassword()` - kiểm tra 6-100 ký tự, có chữ + số
- `getPasswordStrength()` - tính score 0-4 dựa vào complexity

---

#### A.3 Câu 2.1: Login - Unit Tests Backend (5 điểm) - **PHÁT**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Unit test LoginService logic | 5 |
| **File** | `backend/src/test/java/.../LoginServiceTest.java` | - |
| **Test cases** | 25-30 cases | - |
| **Coverage** | Validate username/password, authenticate, error handling | - |

**Test cases**:
- Valid login: correct credentials → user object returned
- Invalid login: wrong password → AuthenticationException
- User not found: username not in DB → UserNotFoundException
- Account locked: too many attempts → AccountLockedException
- Validation errors: short password → ValidationException
- Boundary tests: min/max username/password length
- Negative tests: null inputs, empty strings
- SQL injection attempts → rejected by input validation

---

#### A.4 Câu 3.1: Login - Integration Testing Frontend (5 điểm) - **THÀNH**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Test LoginForm component + mocked authService | 5 |
| **File** | `frontend/src/tests/LoginIntegration.test.jsx` (extend) | - |
| **Test cases** | 20-25 cases | - |
| **Coverage** | Component rendering, form interactions, validation display, API mocking | - |

**Test scenarios**:
- Component renders correctly
- Input field updates work
- Form submission works
- Validation errors display
- Success message displays
- Loading state shows spinner
- Error message displays on API failure
- Button disabled when loading
- Password visibility toggle
- Keyboard navigation (Enter key)

---

#### A.5 Câu 3.1: Login - Integration Testing Backend (5 điểm) - **HUY**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Test LoginController endpoint + HTTP layer | 5 |
| **File** | `backend/src/test/java/.../LoginControllerIntegrationTest.java` | - |
| **Test cases** | 20-25 cases | - |
| **Coverage** | POST /api/auth/login endpoint, status codes, JSON response | - |

**Test cases**:
- 200 OK with valid credentials + token in response
- 400 Bad Request with missing fields
- 400 with invalid username format
- 400 with invalid password format
- 401 Unauthorized with wrong password
- 401 with non-existent user
- 403 Forbidden with locked account
- 500 Internal Server Error handling
- Correct JSON structure in response
- Token format validation

---

#### A.6 Câu 4.1: Login - Mock Testing (5 điểm)

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Frontend Mocking** (2.5 điểm) | Mock authService để test LoginForm độc lập | - |
| **File** | `frontend/src/tests/LoginMockExternal.test.jsx` | - |
| **Assignee** | **HUY** | - |
| **Mục tiêu** | Mock API responses, test UI behavior với different scenarios | - |

**Mock scenarios**:
- Mock successful login response
- Mock failed login (401)
- Mock network timeout
- Mock server error (500)
- Verify authService.login() called with correct params
- Verify localStorage set with token

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Backend Mocking** (2.5 điểm) | Mock UserRepository để test LoginService độc lập | - |
| **File** | `backend/src/test/java/.../LoginServiceMockTest.java` | - |
| **Assignee** | **PHÁT** | - |
| **Mục tiêu** | Mock DB calls, test service logic without DB | - |

**Mock scenarios**:
- Mock userRepository.findByUsername() return user
- Mock passwordEncoder verify password
- Verify repository called correct number of times
- Test behavior when user not found (empty Optional)
- Verify exception thrown on invalid password

---

#### A.7 Câu 5.1: Login - E2E Testing (5 điểm)

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Setup & Configuration** (1 điểm) | Thiết lập Cypress, fixtures, support files | - |
| **Assignee** | **THÀNH** | - |
| **Mục tiêu** | Configure cypress.config.js, setup test data, custom commands | - |

**Setup tasks**:
- Review cypress.config.js baseUrl, timeouts
- Setup fixtures/users.json with test accounts
- Create Cypress custom commands (cy.login())
- Setup Page Object LoginPage.js
- Configure screenshots/videos on failure

| Item | Chi tiết | Điểm |
|------|----------|------|
| **E2E Test Scenarios** (2.5 điểm) | Viết Cypress test cases cho login flow | - |
| **File** | `frontend/src/tests/cypress/e2e/login-scenarios.cy.js` | - |
| **Assignee** | **THÀNH** | - |
| **Mục tiêu** | Test complete login user flow | - |

**Scenarios**:
1. Successful login with valid credentials
2. Failed login - wrong password
3. Validation error - short username
4. Validation error - password without number
5. Toggle password visibility
6. Keyboard navigation (Enter key)
7. Error message displays on failure
8. Success message and redirect on success

| Item | Chi tiết | Điểm |
|------|----------|------|
| **CI/CD Integration** (1.5 điểm) | Tích hợp login tests vào CI/CD pipeline | - |
| **Assignee** | **NGHĨA** | - |
| **Mục tiêu** | GitHub Actions/Jenkins setup chạy login tests tự động | - |

**CI/CD tasks**:
- Add test command to package.json
- Create GitHub Actions workflow (test on push)
- Run unit + integration tests
- Run E2E tests
- Report coverage
- Fail pipeline if tests fail

---

### B. PRODUCT MODULE

#### B.1 Câu 1.2: Product - Phân Tích và Test Scenarios (10 điểm) - **ĐỨC**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Phân tích CRUD + search/filter, viết 40+ test scenarios | 10 |
| **File output** | `docs/TEST_CASE_MATRIX_PRODUCT.md` | - |
| **Test scenarios** | 40+ cases với traceability matrix | - |

**Scenario sets** (40+ cases):
- SC1: Create Product (6 cases - valid, missing fields, validation errors)
- SC2: Read/Display Product (6 cases - list, pagination, details, empty state)
- SC3: Update Product (6 cases - valid, partial update, invalid data, not found)
- SC4: Delete Product (4 cases - with confirm, cancel, not found, last product)
- SC5: Search & Filter (8 cases - by name, case insensitive, combined filters)
- SC6: Pagination (4 cases - next, previous, go to page, with filters)
- SC7: Validation & Error (6 cases - boundary values, timeout, network error)

---

#### B.2 Câu 2.2: Product - Unit Tests Frontend (5 điểm) - **NGHĨA**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Unit test product validation functions | 5 |
| **File** | `frontend/src/tests/validateProduct.test.js` | - |
| **Test cases** | 25-30 cases | - |
| **Coverage** | validateProduct() - name, price, quantity, category, description | - |

**Validation rules test**:
- Product name: 3-100 ký tự, không rỗng
- Price: > 0, valid number
- Quantity: >= 0, valid number
- Category: phải trong list [Electronics, Books, Clothing, Toys, Groceries]
- Description: <= 500 ký tự (optional)
- Boundary tests: 2 (fail), 3 (pass), 100 (pass), 101 (fail)
- Negative tests: empty, null, invalid types
- Combined tests: multiple validation errors

---

#### B.3 Câu 2.2: Product - Unit Tests Backend (5 điểm) - **DANH**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Unit test ProductService business logic | 5 |
| **File** | `backend/src/test/java/.../ProductServiceTest.java` | - |
| **Test cases** | 30-35 cases | - |
| **Coverage** | CRUD operations, pagination, duplicate check, validation | - |

**Test cases**:
- Create: valid product, missing fields, validation errors, duplicate name
- Read: get all, get by ID, get non-existent, pagination
- Update: valid, partial update, invalid data, not found
- Delete: existing product, non-existent, cascade delete checks
- Search: by name (case sensitive), partial match
- Filter: by category, by price range, combined
- Pagination: empty list, first page, last page, invalid page
- Duplicate name check: existsByProductName()
- Boundary values: min/max length, min price, max quantity

---

#### B.4 Câu 3.2: Product - Integration Testing Frontend (5 điểm) - **ĐỨC**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Test ProductManagement component + mocked API | 5 |
| **File** | `frontend/src/tests/ProductComponentsIntegration.test.jsx` (extend) | - |
| **Test cases** | 25-30 cases | - |
| **Coverage** | Component rendering, modal operations, form validation, search/filter, pagination | - |

**Test sets**:
- Rendering (6 cases): component, table, buttons, search, filter, empty message
- Modal (5 cases): open/close, clear form, prefill on edit, submit, validation
- Validation (4 cases): name < 3, price = 0, quantity < 0, category invalid
- Search/Filter (3 cases): search by name, filter by category, combined
- Pagination (2 cases): multiple pages, pagination with filter
- User Feedback (3 cases): success notification, error notification, loading state

---

#### B.5 Câu 3.2: Product - Integration Testing Backend (5 điểm) - **DANH**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Mục tiêu** | Test ProductController endpoints + HTTP layer | 5 |
| **File** | `backend/src/test/java/.../ProductControllerIntegrationTest.java` | - |
| **Test cases** | 25-30 cases | - |
| **Coverage** | REST endpoints (GET, POST, PUT, DELETE), status codes, JSON serialization | - |

**Endpoints tested**:
- GET /api/products → 200, JSON list, pagination
- GET /api/products/{id} → 200 (found), 404 (not found)
- POST /api/products → 201 (created), 400 (validation error)
- PUT /api/products/{id} → 200 (updated), 400, 404, 409 (conflict)
- DELETE /api/products/{id} → 200 (deleted), 404
- GET /api/products?name=X → 200, filtered results
- GET /api/products?category=X → 200, filtered results
- GET /api/products?page=0&size=10 → 200, paginated

---

#### B.6 Câu 4.2: Product - Mock Testing (5 điểm)

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Frontend Mocking** (2.5 điểm) | Mock productService để test ProductManagement độc lập | - |
| **File** | `frontend/src/tests/ProductMockExternal.test.jsx` | - |
| **Assignee** | **NGHĨA** | - |
| **Mục tiêu** | Mock API responses, test component with different data scenarios | - |

**Mock scenarios**:
- Mock getAllProducts() return list
- Mock getProductById() return single product
- Mock createProduct() success/failure
- Mock updateProduct() success/failure
- Mock deleteProduct() success/failure
- Test loading state during API call
- Test error display on API failure
- Verify API methods called with correct params

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Backend Mocking** (2.5 điểm) | Mock ProductRepository để test ProductService độc lập | - |
| **File** | `backend/src/test/java/.../ProductServiceMockTest.java` | - |
| **Assignee** | **DANH** | - |
| **Mục tiêu** | Mock DB calls, test service logic without database | - |

**Mock scenarios**:
- Mock productRepository.save() return saved product
- Mock productRepository.findById() return product / empty
- Mock productRepository.findAll() return list
- Mock productRepository.existsByProductName() return true/false
- Verify repository methods called correct times
- Test exception handling when repository throws
- ArgumentCaptor to verify saved object properties

---

#### B.7 Câu 5.2: Product - E2E Testing (3.5 điểm)

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Setup Page Object Model** (1 điểm) | Tạo ProductPage.js POM | - |
| **File** | `frontend/src/tests/cypress/pages/ProductPage.js` | - |
| **Assignee** | **DANH** | - |
| **Mục tiêu** | Tổng hợp selectors, tạo helper methods | - |

**POM structure**:
```javascript
class ProductPage {
  // Selectors
  searchInput = 'input[placeholder="Tìm kiếm"]'
  categoryFilter = 'select[name="category"]'
  addProductBtn = 'button:contains("Thêm Sản Phẩm")'
  table = 'table'
  
  // Actions
  searchProduct(name) { ... }
  filterByCategory(category) { ... }
  clickAddProduct() { ... }
  clickEditProduct(row) { ... }
  deleteProduct(row) { ... }
}
```

| Item | Chi tiết | Điểm |
|------|----------|------|
| **E2E Test Scenarios** (2.5 điểm) | Viết Cypress test cases cho product CRUD | - |
| **File** | `frontend/src/tests/cypress/e2e/product-e2e-scenarios.cy.js` | - |
| **Assignee** | **DANH** | - |
| **Mục tiêu** | Test complete product management user flow | - |

**Scenarios**:
1. Create product with valid data
2. Create product - validation error
3. View product list and pagination
4. Search product by name
5. Filter product by category
6. Edit product successfully
7. Delete product with confirm
8. Cancel delete product
9. Combined search + filter
10. Error handling on network failure

| Item | Chi tiết | Điểm |
|------|----------|------|
| **CI/CD Integration** (1.5 điểm) | Tích hợp product tests vào pipeline | - |
| **Assignee** | **PHÁT** | - |
| **File** | `.github/workflows/test.yml` (extend) | - |

---

### C. PHẦN MỞ RỘNG (BONUS)

#### C.1 Performance Testing (5 điểm) - **ĐỨC**

| Item | Chi tiết | Điểm |
|------|----------|------|
| **Setup k6/JMeter** (2 điểm) | Cài đặt, configure performance test tool | - |
| **Load test Login** (3 điểm) | Load test endpoint login với 100, 500, 1000 users | - |
| **Stress test** | Tìm breaking point của API | - |
| **Metrics** | Response time (p95, p99), error rate, throughput | - |

**Deliverables**:
- k6 script: `performance-tests/scripts/login-test.js`
- Test results: response times, error rates
- Analysis: identify bottlenecks

---

#### C.2 Security Testing (10 điểm)

#### C.2.1 Common Vulnerabilities (5 điểm)

| Vulnerability | Assignee | Task |
|----------------|----------|------|
| **SQL Injection** (1.25 điểm) | **NGHĨA** | Nhập `' OR '1'='1` vào username/password, verify reject |
| **XSS** (1.25 điểm) | **DANH** | Nhập `<script>alert(1)</script>` vào product name, verify escape |
| **CSRF** (1.25 điểm) | **PHÁT** | Test CSRF token validation, POST from external site rejected |
| **Authentication Bypass** (1.25 điểm) | **THÀNH** | Test empty fields, direct API without token, invalid token |

**Testing approach**:
- Manual penetration testing
- Automated security scanning (if available)
- Test case per vulnerability type
- Document findings and mitigations

#### C.2.2 Input Validation & Sanitization (5 điểm)

| Item | Assignee | Task | Điểm |
|------|----------|------|------|
| **Input Validation** (2.5 điểm) | **HUY** | Test length limits, type validation, required fields | - |
| **Output Sanitization** (2.5 điểm) | **ĐỨC** | Test XSS escape, HTML encoding, safe display | - |

**Sanitization test cases**:
- escapeHTML() function: `<` → `&lt;`, `"` → `&quot;`
- Remove script tags from saved data
- Prevent event handler injection
- Encode output when displaying user data

---

## III. TÓNG KẾT ĐIỂM SỐ

### A. Bảng Điểm Chi Tiết

#### Login Module (Phát, Thành, Huy) - 45 điểm

| Item | Assignee | Điểm | Status |
|------|----------|------|--------|
| 1.1 Test Scenarios | Huy | 10 | |
| 2.1 Frontend Unit | Thành | 5 | |
| 2.1 Backend Unit | Phát | 5 | |
| 3.1 Frontend Integration | Thành | 5 | |
| 3.1 Backend Integration | Huy | 5 | |
| 4.1 Frontend Mocking | Huy | 2.5 | |
| 4.1 Backend Mocking | Phát | 2.5 | |
| 5.1 E2E Setup | Thành | 1 | |
| 5.1 E2E Scenarios | Thành | 2.5 | |
| 5.1 CI/CD Integration | Nghĩa | 1.5 | |
| **Subtotal Login** | | **40** | |

#### Product Module (Danh, Nghĩa, Đức) - 45 điểm

| Item | Assignee | Điểm | Status |
|------|----------|------|--------|
| 1.2 Test Scenarios | Đức | 10 | |
| 2.2 Frontend Unit | Nghĩa | 5 | |
| 2.2 Backend Unit | Danh | 5 | |
| 3.2 Frontend Integration | Đức | 5 | |
| 3.2 Backend Integration | Danh | 5 | |
| 4.2 Frontend Mocking | Nghĩa | 2.5 | |
| 4.2 Backend Mocking | Danh | 2.5 | |
| 5.2 E2E Setup (POM) | Danh | 1 | |
| 5.2 E2E Scenarios | Danh | 2.5 | |
| 5.2 CI/CD Integration | Phát | 1.5 | |
| **Subtotal Product** | | **40** | |

#### Bonus & Special Tasks

| Item | Assignee | Điểm | Status |
|------|----------|------|--------|
| Performance Testing | Đức | 5 | Bonus |
| Security - Common Vulnerabilities | Mixed | 5 | Bonus |
| Security - Input Validation | Huy | 2.5 | Bonus |
| Security - Output Sanitization | Đức | 2.5 | Bonus |
| **Subtotal Bonus** | | **15** | |

### B. Tóng Kết Per Person

| Người | Module | Điểm Chính | Bonus | Tổng |
|------|--------|-----------|-------|------|
| **Phát** | Login, Product CI/CD | 10 | - | 10 |
| **Thành** | Login Frontend | 13.5 | - | 13.5 |
| **Huy** | Login Tests | 17.5 | 2.5 | 20 |
| **Nghĩa** | Product Unit + CI/CD | 10 | 2.5 | 12.5 |
| **Danh** | Product Backend | 15 | - | 15 |
| **Đức** | Product Scenarios + Bonus | 15 | 5 | 20 |
| **TỔNG** | | 81 | 10 | **91 điểm** |

---

## IV. TIMELINE

### Week 1: Phân Tích & Unit Tests
- **Ngày 1-2**: Test scenarios (Huy, Đức)
- **Ngày 3-4**: Frontend unit tests (Thành, Nghĩa)
- **Ngày 5**: Backend unit tests (Phát, Danh)

### Week 2: Integration & Mocking
- **Ngày 6-7**: Integration tests (Thành, Huy, Đức, Danh)
- **Ngày 8**: Mock tests (Huy, Phát, Nghĩa, Danh)
- **Ngày 9**: Review & fix failures

### Week 3: E2E & CI/CD
- **Ngày 10-11**: E2E tests setup & scenarios (Thành, Danh)
- **Ngày 12**: CI/CD integration (Nghĩa, Phát)

### Week 4: Bonus & Report
- **Ngày 13-14**: Performance testing (Đức)
- **Ngày 15**: Security testing (Mixed)
- **Ngày 16-17**: Report writing (Phát, Huy, Nghĩa)
- **Ngày 18**: Demo & presentation

---

## V. DELIVERABLES CHECKLIST

### Documentation
- [ ] TEST_CASE_MATRIX_LOGIN.md (Huy)
- [ ] TEST_CASE_MATRIX_PRODUCT.md (Đức)
- [ ] THANH_RESPONSIBILITIES_EXPLAINED.md (Done)
- [ ] DUC_RESPONSIBILITIES_EXPLAINED.md (Done)
- [ ] Final Report (Phát, Huy, Nghĩa)

### Frontend Tests
- [ ] validation.test.js - Login (Thành)
- [ ] LoginIntegration.test.jsx (Thành)
- [ ] LoginMockExternal.test.jsx (Huy)
- [ ] validateProduct.test.js (Nghĩa)
- [ ] ProductComponentsIntegration.test.jsx (Đức)
- [ ] ProductMockExternal.test.jsx (Nghĩa)
- [ ] security/sanitization.test.js (Đức)

### Backend Tests
- [ ] LoginServiceTest.java (Phát)
- [ ] LoginServiceMockTest.java (Phát)
- [ ] LoginControllerIntegrationTest.java (Huy)
- [ ] ProductServiceTest.java (Danh)
- [ ] ProductServiceMockTest.java (Danh)
- [ ] ProductControllerIntegrationTest.java (Danh)

### E2E Tests
- [ ] cypress/e2e/login-scenarios.cy.js (Thành)
- [ ] cypress/e2e/product-e2e-scenarios.cy.js (Danh)
- [ ] cypress/pages/ProductPage.js (Danh)

### Performance & Security
- [ ] performance-tests/scripts/product-test.js (Đức)
- [ ] security/sql-injection-tests.md (Nghĩa)
- [ ] security/xss-tests.md (Danh)
- [ ] security/csrf-tests.md (Phát)

### CI/CD
- [ ] .github/workflows/test.yml (Nghĩa, Phát)
- [ ] GitHub Actions for Login (Nghĩa)
- [ ] GitHub Actions for Product (Phát)

---

## VI. COMMUNICATION & ESCALATION

### Standup Meeting
- Daily 10:00 AM (nếu cần)
- Report blockers, risks, progress

### Weekly Review
- Đánh giá progress, adjust plan

### Contact Person Per Module
- **Login Module Lead**: Huy
- **Product Module Lead**: Danh
- **Performance/Security Lead**: Đức
- **CI/CD Lead**: Phát

---

## VII. NOTES

1. **Existing Code**: 
   - validation.test.js đã có ~400 lines, chỉ cần review
   - ProductComponentsIntegration.test.jsx có sẵn, cần extend
   - Cypress config sẵn, chỉ cần thêm test cases

2. **Test Data**:
   - Frontend fixtures: `cypress/fixtures/users.json`
   - Backend test data: tạo via @Before hoặc test data builder

3. **Database**:
   - Use H2 in-memory DB cho backend tests (mặc định Spring Boot test)
   - Mock repository trong unit tests
   - Real DB chỉ dùng cho integration tests (optional)

4. **Performance**:
   - Load test không cần chạy lúc nào, có thể chạy separate
   - Stress test breaking point tìm trên staging

5. **Security**:
   - Penetration testing không cần chạy production
   - Manual testing + automated unit tests đủ

---

Nếu có câu hỏi về phân công hoặc chi tiết công việc, liên hệ mentor hoặc team lead tương ứng.
