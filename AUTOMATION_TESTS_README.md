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
