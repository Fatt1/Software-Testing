# 🧪 Automation Testing Guide

## Overview

Dự án có ba loại test automation được thiết lập:

1. **Unit Tests (Jest)** - Kiểm tra từng đơn vị code
2. **Integration Tests (Jest + React Testing Library)** - Kiểm tra tương tác giữa các component
3. **E2E Tests (Cypress)** - Kiểm tra toàn bộ luồng người dùng

---

## 📋 Test Structure

```
frontend/
├── src/tests/
│   ├── LoginIntegration.test.jsx       (23 tests - Integration)
│   ├── ProductComponentsIntegration.test.jsx (23 tests - Integration)
│   ├── LoginMockExternal.test.jsx      (Mock tests)
│   ├── ProductMockExternal.test.jsx    (Mock tests)
│   ├── validation.test.js              (Unit tests)
│   ├── validateProduct.test.js         (Unit tests)
│   └── cypress/
│       ├── e2e/
│       │   ├── login.cy.js             (Setup & Configuration)
│       │   └── login-scenarios.cy.js   (E2E Scenarios - 41 tests)
│       ├── pages/
│       │   └── LoginPage.js            (Page Object Model)
│       ├── fixtures/
│       │   └── users.json              (Test data)
│       └── support/
│           └── e2e.js                  (Cypress support)
```

---

## 🚀 Running Tests Locally

### 1. Integration Tests (Jest)

**Run all tests:**
```bash
cd frontend
npm test
```

**Run specific test file:**
```bash
npm test -- LoginIntegration.test.jsx
npm test -- ProductComponentsIntegration.test.jsx
```

**Run tests with coverage:**
```bash
npm test -- --coverage
```

**Watch mode (auto-run on file changes):**
```bash
npm test -- --watch
```

---

### 2. E2E Tests (Cypress)

**Prerequisites:**
- Ensure dev server is running: `npm run dev` (in another terminal)
- Server should be running on `http://localhost:5173`

**Interactive mode (recommended for development):**
```bash
npx cypress open
```

Then select:
- E2E Testing
- Choose browser (Chrome, Edge, Firefox)
- Select test file to run

**Headless mode (CI/CD):**
```bash
npx cypress run
```

**Run specific E2E test file:**
```bash
npx cypress run --spec "src/tests/cypress/e2e/login-scenarios.cy.js"
```

**Generate video of test run:**
```bash
npx cypress run --spec "src/tests/cypress/e2e/login-scenarios.cy.js" --record
```

---

### 3. Generate Test Reports

**Windows:**
```bash
cd frontend
.\generate-reports.bat
```

**macOS/Linux:**
```bash
cd frontend
bash generate-reports.sh
```

This will:
- Run all Jest integration tests with coverage
- Start dev server
- Run all Cypress E2E tests
- Generate HTML report
- Create test reports in `test-reports/` folder

---

## 📊 Test Coverage

### Jest Integration Tests (46 total)

**LoginIntegration.test.jsx (23 tests):**
- ✓ Rendering & User Interactions (10 tests)
- ✓ Form Submission & API Calls (6 tests)
- ✓ Error Handling & Success Messages (7 tests)

**ProductComponentsIntegration.test.jsx (23 tests):**
- ✓ Product list rendering
- ✓ Search & filtering
- ✓ CRUD operations
- ✓ Error handling

### Cypress E2E Tests (41 tests)

**login-scenarios.cy.js:**

1. **Scenario 1: Complete Login Flow (1 point, 10 tests)**
   - Open login page
   - Enter username & password
   - Submit form
   - Handle button states
   - Clear & re-enter data

2. **Scenario 2: Validation Messages (0.5 point, 8 tests)**
   - Empty form validation
   - Invalid format detection
   - Error message display
   - Field correction

3. **Scenario 3: Success/Error Flows (0.5 point, 8 tests)**
   - Successful login
   - Failed login
   - Error messages
   - Retry logic
   - Network error handling

4. **Scenario 4: UI Elements Interactions (0.5 point, 10 tests)**
   - Remember me checkbox
   - Password visibility toggle
   - Form links
   - Button states
   - Responsive interactions

5. **Scenario 5: Advanced User Flows (Bonus, 5 tests)**
   - Complete journey with all features
   - Error handling & recovery
   - Multiple rapid interactions

---

## 🔄 GitHub Actions Workflow

File: `.github/workflows/automation-tests.yml`

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Manual trigger (workflow_dispatch)

**What it does:**
1. Checks out code
2. Sets up Node.js (18.x, 20.x)
3. Installs dependencies
4. Runs Jest integration tests with coverage
5. Builds frontend
6. Starts dev server
7. Runs Cypress E2E tests
8. Uploads artifacts (coverage, screenshots, videos)
9. Generates test report summary
10. Comments on PR with results

**View Results:**
- Go to repository's **Actions** tab
- Click on workflow run
- View logs and artifacts

---

## 📝 Test Configuration Files

### `cypress.config.js`
```javascript
- baseUrl: http://localhost:5173
- viewportWidth: 1280
- viewportHeight: 720
- specPattern: src/tests/cypress/e2e/**/*.cy.js
- supportFile: src/tests/cypress/support/e2e.js
- fixturesFolder: src/tests/cypress/fixtures
```

### `jest.config.cjs`
```javascript
- testEnvironment: jsdom
- setupFilesAfterEnv: jest.setup.cjs
- testPathIgnorePatterns: ['/node_modules/', '/cypress/']
```

### `package.json` scripts
```json
{
  "test": "jest",
  "test:watch": "jest --watch",
  "test:coverage": "jest --coverage",
  "e2e": "cypress run",
  "e2e:open": "cypress open",
  "dev": "vite",
  "build": "vite build"
}
```

---

## 🛠️ Test Data & Fixtures

### Test Users (`fixtures/users.json`)
```json
{
  "validUser": {
    "username": "admin",
    "password": "admin123",
    "email": "admin@example.com"
  },
  "invalidUser": {
    "username": "invaliduser",
    "password": "wrongpass"
  }
}
```

---

## 🔍 Page Object Model

### LoginPage.js
Centralizes all selectors and interactions for login page:

```javascript
class LoginPage {
  get usernameInput() { }
  get passwordInput() { }
  get loginButton() { }
  
  navigateToLoginPage() { }
  enterUsername(username) { }
  enterPassword(password) { }
  login(username, password) { }
  verifyErrorMessage(text) { }
}
```

**Benefits:**
- Easy to maintain selectors
- Reusable in multiple tests
- Reduces test code duplication

---

## 🐛 Debugging Tests

### Jest Debug
```bash
# Run specific test with debugging
node --inspect-brk node_modules/.bin/jest --runInBand LoginIntegration.test.jsx
```

### Cypress Debug
```bash
# Open DevTools in Cypress
npx cypress open
# Then click the DevTools icon during test run
```

### View Test Reports
```bash
# Open HTML report in browser
open test-reports/test-report-*/index.html  # macOS
start test-reports/test-report-*/index.html # Windows
```

---

## 📈 Metrics & Reports

After running `generate-reports.sh` or `generate-reports.bat`:

**Files generated:**
- `jest/results.json` - Jest test results in JSON
- `jest/coverage/` - Code coverage report
- `cypress/results.json` - Cypress test results
- `cypress/screenshots/` - Failed test screenshots
- `cypress/videos/` - Test run videos
- `index.html` - Visual report summary
- `SUMMARY.md` - Detailed markdown report

---

## ✅ Checklist for Test Automation

- [x] Unit tests written
- [x] Integration tests for components
- [x] Mock tests for external services
- [x] E2E tests for complete flows
- [x] Test data fixtures
- [x] Page Object Model implementation
- [x] GitHub Actions workflow
- [x] Test report generation
- [x] Coverage tracking
- [x] CI/CD integration

---

## 🎯 Next Steps

1. **Run tests locally:**
   ```bash
   npm test
   npx cypress open
   ```

2. **Generate reports:**
   ```bash
   ./generate-reports.sh  # or .bat on Windows
   ```

3. **Push to GitHub:**
   - GitHub Actions will automatically run tests
   - Check workflow in Actions tab
   - Review test reports and coverage

4. **Improve coverage:**
   - Add more test cases
   - Cover edge cases
   - Increase component coverage

---

## 📚 Resources

- [Jest Documentation](https://jestjs.io/)
- [React Testing Library](https://testing-library.com/react)
- [Cypress Documentation](https://docs.cypress.io/)
- [Page Object Model Pattern](https://www.cypress.io/blog/2019/01/03/page-object-model/)

---

## 🤝 Contributing

When adding new features:
1. Write tests first (TDD)
2. Run local tests: `npm test && npm run e2e`
3. Generate reports: `./generate-reports.sh`
4. Push to GitHub
5. Verify GitHub Actions pass
6. Create Pull Request

---

## 📞 Support

For issues or questions about automation tests, check:
- Test output logs
- GitHub Actions logs
- Cypress screenshots/videos
- Jest coverage reports

---

## 🔄 CI/CD Pipeline Details

### GitHub Actions Workflow Architecture

**File**: `.github/workflows/automation-tests.yml`

**Workflow Stages:**

#### Stage 1: Environment Setup
```yaml
- name: Checkout repository
  uses: actions/checkout@v3
  
- name: Setup Node.js
  uses: actions/setup-node@v3
  with:
    node-version: '18.x'
    cache: 'npm'
    
- name: Cache dependencies
  uses: actions/cache@v3
  with:
    path: ~/.npm
    key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
```

**Benefits:**
- ✅ Consistent environment across runs
- ✅ Faster builds with dependency caching
- ✅ Reproducible test results

#### Stage 2: Install Dependencies
```yaml
- name: Install dependencies
  run: |
    cd frontend
    npm ci --prefer-offline
```

**Why `npm ci` instead of `npm install`?**
- Faster installation (up to 2x)
- Uses exact versions from package-lock.json
- Automatically removes node_modules before install
- Better for CI/CD environments

#### Stage 3: Jest Integration Tests
```yaml
- name: Run Jest tests with coverage
  run: |
    cd frontend
    npm test -- --coverage --ci --maxWorkers=2
    
- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./frontend/coverage/lcov.info
    flags: unittests
    name: codecov-umbrella
```

**Coverage Thresholds:**
```javascript
// jest.config.cjs
module.exports = {
  coverageThreshold: {
    global: {
      branches: 70,
      functions: 70,
      lines: 70,
      statements: 70
    }
  }
};
```

#### Stage 4: Build Application
```yaml
- name: Build frontend
  run: |
    cd frontend
    npm run build
  env:
    NODE_ENV: production
    VITE_API_URL: http://localhost:8080
```

**Build Optimizations:**
- Tree-shaking to remove unused code
- Code splitting for lazy loading
- Minification with Terser
- Asset optimization (images, fonts)

#### Stage 5: Start Development Server
```yaml
- name: Start dev server in background
  run: |
    cd frontend
    npm run dev &
    npx wait-on http://localhost:5173 --timeout 60000
```

**Why wait-on?**
- Ensures server is fully ready before tests
- Prevents flaky test failures
- Configurable timeout and retry logic

#### Stage 6: Cypress E2E Tests
```yaml
- name: Run Cypress E2E tests
  uses: cypress-io/github-action@v5
  with:
    working-directory: frontend
    browser: chrome
    record: true
    parallel: true
  env:
    CYPRESS_RECORD_KEY: ${{ secrets.CYPRESS_RECORD_KEY }}
```

**Cypress Cloud Features:**
- Test result history and trends
- Parallelization across multiple machines
- Video recordings and screenshots
- Flaky test detection

#### Stage 7: Upload Artifacts
```yaml
- name: Upload test artifacts
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: test-results
    path: |
      frontend/coverage/
      frontend/cypress/screenshots/
      frontend/cypress/videos/
      frontend/test-reports/
    retention-days: 30
```

**Artifact Types:**
- **Coverage reports**: HTML + LCOV format
- **Screenshots**: Failed test evidence
- **Videos**: Full test execution recording
- **Test reports**: JSON + HTML summaries

#### Stage 8: Report Results
```yaml
- name: Comment PR with test results
  if: github.event_name == 'pull_request'
  uses: actions/github-script@v6
  with:
    script: |
      const testResults = require('./frontend/test-reports/summary.json');
      const body = `
      ## Test Results 🧪
      
      **Jest Tests**: ${testResults.jest.passed}/${testResults.jest.total} passed
      **Cypress Tests**: ${testResults.cypress.passed}/${testResults.cypress.total} passed
      **Coverage**: ${testResults.coverage.percentage}%
      `;
      
      github.rest.issues.createComment({
        issue_number: context.issue.number,
        owner: context.repo.owner,
        repo: context.repo.repo,
        body: body
      });
```

---

## 🛠️ Advanced Testing Patterns

### 1. Data-Driven Testing

**Concept**: Run same test with multiple data sets

**Example (Jest):**
```javascript
const testCases = [
  { input: 'admin', expected: true },
  { input: 'a', expected: false },  // Too short
  { input: 'a'.repeat(51), expected: false },  // Too long
  { input: 'user@123', expected: false },  // Invalid chars
];

test.each(testCases)(
  'validateUsername($input) should return $expected',
  ({ input, expected }) => {
    expect(validateUsername(input)).toBe(expected);
  }
);
```

**Example (Cypress):**
```javascript
const users = [
  { username: 'admin', password: 'admin123', shouldPass: true },
  { username: 'invalid', password: 'wrong', shouldPass: false },
];

users.forEach(user => {
  it(`should ${user.shouldPass ? 'succeed' : 'fail'} with ${user.username}`, () => {
    cy.visit('/login');
    cy.get('#username').type(user.username);
    cy.get('#password').type(user.password);
    cy.get('button[type="submit"]').click();
    
    if (user.shouldPass) {
      cy.url().should('include', '/products');
    } else {
      cy.contains('Invalid credentials').should('be.visible');
    }
  });
});
```

### 2. Mock Service Worker (MSW) for API Mocking

**Setup:**
```javascript
// src/mocks/handlers.js
import { rest } from 'msw';

export const handlers = [
  rest.post('/api/auth/login', (req, res, ctx) => {
    const { userName, password } = req.body;
    
    if (userName === 'admin' && password === 'admin123') {
      return res(
        ctx.status(200),
        ctx.json({
          token: 'mock-jwt-token',
          message: 'Login successful',
          userDto: { userName: 'admin', email: 'admin@example.com' }
        })
      );
    }
    
    return res(
      ctx.status(401),
      ctx.json({ message: 'Invalid credentials' })
    );
  }),
  
  rest.get('/api/products', (req, res, ctx) => {
    const page = req.url.searchParams.get('page') || '0';
    const size = req.url.searchParams.get('size') || '10';
    
    return res(
      ctx.status(200),
      ctx.json({
        content: mockProducts.slice(parseInt(page) * parseInt(size), (parseInt(page) + 1) * parseInt(size)),
        totalPages: Math.ceil(mockProducts.length / parseInt(size)),
        totalElements: mockProducts.length
      })
    );
  })
];
```

**Usage in Tests:**
```javascript
import { server } from './mocks/server';

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('handles server error', async () => {
  server.use(
    rest.get('/api/products', (req, res, ctx) => {
      return res(ctx.status(500));
    })
  );
  
  render(<ProductManagement />);
  
  await waitFor(() => {
    expect(screen.getByText(/error loading products/i)).toBeInTheDocument();
  });
});
```

### 3. Visual Regression Testing

**Using Cypress + Percy:**
```javascript
import '@percy/cypress';

describe('Visual Regression Tests', () => {
  it('should match login page snapshot', () => {
    cy.visit('/login');
    cy.percySnapshot('Login Page');
  });
  
  it('should match product table snapshot', () => {
    cy.visit('/products');
    cy.wait(1000);  // Wait for data load
    cy.percySnapshot('Product Management Page', {
      widths: [768, 1024, 1280]  // Test responsive views
    });
  });
});
```

### 4. Accessibility Testing

**Using jest-axe:**
```javascript
import { axe, toHaveNoViolations } from 'jest-axe';

expect.extend(toHaveNoViolations);

test('LoginForm should have no accessibility violations', async () => {
  const { container } = render(<LoginForm />);
  const results = await axe(container);
  
  expect(results).toHaveNoViolations();
});
```

**Using cypress-axe:**
```javascript
import 'cypress-axe';

describe('Accessibility Tests', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.injectAxe();
  });
  
  it('should have no detectable accessibility violations', () => {
    cy.checkA11y();
  });
  
  it('should have proper ARIA labels', () => {
    cy.get('#username').should('have.attr', 'aria-label');
    cy.get('#password').should('have.attr', 'aria-label');
  });
});
```

---

## 📈 Coverage Analysis Strategies

### Understanding Coverage Metrics

**1. Statement Coverage**
- Measures which statements were executed
- Goal: >80%
- Example:
```javascript
function divide(a, b) {
  if (b === 0) return null;  // Statement 1
  return a / b;              // Statement 2
}

// Test: divide(10, 2) covers Statement 2 only (50% coverage)
// Test: divide(10, 0) covers Statement 1 only (50% coverage)
// Both tests together = 100% statement coverage
```

**2. Branch Coverage**
- Measures which decision branches were taken
- Goal: >75%
- Example:
```javascript
function validateAge(age) {
  if (age < 18) {           // Branch 1: true
    return 'minor';         //   Path A
  } else {                  // Branch 1: false
    return 'adult';         //   Path B
  }
}

// Need tests for both age < 18 and age >= 18 for 100% branch coverage
```

**3. Function Coverage**
- Measures which functions were called
- Goal: >70%
- All exported functions should have at least one test

**4. Line Coverage**
- Similar to statement coverage
- Measures which lines of code executed
- Goal: >80%

### Improving Coverage

**Strategy 1: Identify Uncovered Code**
```bash
# Generate detailed coverage report
npm test -- --coverage --coverageReporters=html

# Open in browser
open coverage/lcov-report/index.html

# Look for red (uncovered) lines
```

**Strategy 2: Test Edge Cases**
```javascript
describe('validateProduct edge cases', () => {
  test('should handle minimum valid price', () => {
    expect(validateProduct({ price: 0.01 })).toBeTruthy();
  });
  
  test('should handle maximum valid price', () => {
    expect(validateProduct({ price: 999999999 })).toBeTruthy();
  });
  
  test('should handle empty string inputs', () => {
    expect(validateProduct({ name: '' })).toBeFalsy();
  });
  
  test('should handle null/undefined', () => {
    expect(validateProduct(null)).toBeFalsy();
    expect(validateProduct(undefined)).toBeFalsy();
  });
});
```

**Strategy 3: Test Error Paths**
```javascript
test('should handle network errors gracefully', async () => {
  const mockError = new Error('Network error');
  jest.spyOn(axios, 'get').mockRejectedValue(mockError);
  
  render(<ProductManagement />);
  
  await waitFor(() => {
    expect(screen.getByText(/failed to load/i)).toBeInTheDocument();
  });
  
  expect(axios.get).toHaveBeenCalledTimes(1);
});
```

---

## 📝 Test Maintenance Best Practices

### 1. Test Naming Conventions

**Good Naming:**
```javascript
// ✅ Clear and descriptive
describe('LoginForm', () => {
  test('should display error message when username is empty', () => {});
  test('should disable submit button while request is pending', () => {});
  test('should call onLoginSuccess callback with user data', () => {});
});

// ❌ Vague and unhelpful
describe('LoginForm', () => {
  test('test 1', () => {});
  test('it works', () => {});
  test('check button', () => {});
});
```

**Pattern**: `should [expected behavior] when [condition]`

### 2. Test Organization

**File Structure:**
```
src/
├── components/
│   ├── LoginForm.jsx
│   └── ProductManagement.jsx
├── services/
│   ├── authService.js
│   └── productService.js
└── tests/
    ├── components/
    │   ├── LoginForm.test.jsx
    │   └── ProductManagement.test.jsx
    └── services/
        ├── authService.test.js
        └── productService.test.js
```

**Group Related Tests:**
```javascript
describe('ProductManagement', () => {
  describe('Rendering', () => {
    test('should render product table', () => {});
    test('should render search input', () => {});
    test('should render add button', () => {});
  });
  
  describe('Search Functionality', () => {
    test('should filter products by name', () => {});
    test('should show no results message', () => {});
    test('should clear search on button click', () => {});
  });
  
  describe('CRUD Operations', () => {
    test('should create new product', () => {});
    test('should update existing product', () => {});
    test('should delete product', () => {});
  });
});
```

### 3. DRY (Don't Repeat Yourself)

**Extract Common Setup:**
```javascript
// ❌ Repetitive
test('test 1', () => {
  const { getByText } = render(<ProductManagement />);
  fireEvent.click(getByText('Add Product'));
  // ...
});

test('test 2', () => {
  const { getByText } = render(<ProductManagement />);
  fireEvent.click(getByText('Add Product'));
  // ...
});

// ✅ Reusable helper
const renderAndOpenAddDialog = () => {
  const utils = render(<ProductManagement />);
  fireEvent.click(utils.getByText('Add Product'));
  return utils;
};

test('test 1', () => {
  const { getByLabelText } = renderAndOpenAddDialog();
  // ...
});

test('test 2', () => {
  const { getByLabelText } = renderAndOpenAddDialog();
  // ...
});
```

### 4. Test Data Factories

**Create Reusable Test Data:**
```javascript
// testUtils/factories.js
export const createMockProduct = (overrides = {}) => ({
  id: 1,
  productName: 'Test Product',
  price: 1000000,
  quantity: 10,
  category: 'Điện tử',
  description: 'Test description with enough length',
  ...overrides
});

export const createMockUser = (overrides = {}) => ({
  userName: 'testuser',
  email: 'test@example.com',
  ...overrides
});

// Usage in tests
test('should display product details', () => {
  const product = createMockProduct({ productName: 'Laptop' });
  render(<ProductDetails product={product} />);
  expect(screen.getByText('Laptop')).toBeInTheDocument();
});
```

### 5. Async Test Patterns

**Best Practices:**
```javascript
// ✅ Use waitFor for async assertions
test('should load products on mount', async () => {
  render(<ProductManagement />);
  
  await waitFor(() => {
    expect(screen.getByText('Laptop')).toBeInTheDocument();
  });
});

// ✅ Use findBy queries (includes implicit wait)
test('should show error message', async () => {
  render(<ProductManagement />);
  
  const errorMessage = await screen.findByText(/error loading/i);
  expect(errorMessage).toBeInTheDocument();
});

// ❌ Avoid arbitrary wait times
test('bad example', async () => {
  render(<ProductManagement />);
  await new Promise(resolve => setTimeout(resolve, 1000));  // ❌ Flaky!
  expect(screen.getByText('Laptop')).toBeInTheDocument();
});
```

---

## 🚀 Performance Optimization

### Test Execution Speed

**1. Run Tests in Parallel:**
```bash
# Jest (default behavior)
npm test -- --maxWorkers=4

# Cypress
npx cypress run --parallel --record --key <record_key>
```

**2. Run Only Changed Tests:**
```bash
# Jest watch mode
npm test -- --watch

# Only tests related to changed files
npm test -- --onlyChanged
```

**3. Skip Slow Tests Locally:**
```javascript
// Mark slow tests
test.skip('slow integration test', async () => {
  // This test only runs in CI
});

// Or use test.concurrent for parallel execution
test.concurrent('test 1', async () => { /* ... */ });
test.concurrent('test 2', async () => { /* ... */ });
```

---

## 📊 Metrics & Reporting

### Key Test Metrics

**1. Test Execution Time**
- Target: <5 minutes for full suite
- Monitor trends over time
- Identify slow tests

**2. Flaky Test Rate**
- Target: <2% flaky rate
- Track tests that fail intermittently
- Fix or quarantine flaky tests

**3. Test Coverage**
- Target: >70% overall
- Critical paths: 100% coverage
- Monitor coverage trends

**4. Bug Detection Rate**
- Track bugs found by tests vs manual QA
- Measure test effectiveness
- Identify gaps in test coverage

### Generating Reports

**Jest HTML Report:**
```bash
npm test -- --coverage --coverageReporters=html lcov text
open coverage/lcov-report/index.html
```

**Cypress Dashboard:**
```bash
npx cypress run --record --key <your-key>
# View results at: https://dashboard.cypress.io
```

**Custom Reporter:**
```javascript
// jest.config.cjs
module.exports = {
  reporters: [
    'default',
    ['jest-html-reporter', {
      pageTitle: 'Test Report',
      outputPath: 'test-reports/index.html',
      includeFailureMsg: true,
      includeConsoleLog: true
    }]
  ]
};
```
