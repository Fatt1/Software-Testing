# TÓM TẮT PHÂN CÔNG - Quick Reference

## Phân Công Nhanh

### Login: Phát, Thành, Huy

**Phát (10 điểm)**:
- Backend Unit Tests (LoginService) - 5 điểm
- Backend Mocking - 2.5 điểm  
- CI/CD Integration Product - 1.5 điểm
- CSRF Security Test - 1.25 điểm

**Thành (13.5 điểm)**:
- Frontend Unit Tests (validation) - 5 điểm
- Frontend Integration Tests - 5 điểm
- E2E Setup & Scenarios - 3.5 điểm

**Huy (20 điểm)**:
- Test Scenarios - 10 điểm
- Backend Integration Tests - 5 điểm
- Frontend Mocking - 2.5 điểm
- Input Validation Security - 2.5 điểm

### Product: Nghĩa, Danh, Đức

**Danh (15 điểm)**:
- Backend Unit Tests (ProductService) - 5 điểm
- Backend Integration Tests - 5 điểm
- Backend Mocking - 2.5 điểm
- E2E Setup (POM) + Scenarios - 3.5 điểm
- XSS Security Test - 1.25 điểm

**Nghĩa (12.5 điểm)**:
- Frontend Unit Tests (validateProduct) - 5 điểm
- Frontend Mocking - 2.5 điểm
- CI/CD Integration Login - 1.5 điểm
- SQL Injection Security - 1.25 điểm
- Input Validation Security - 2.5 điểm (shared with Huy)
- Part of overall 3.75 for input validation 

**Đức (20 điểm)**:
- Test Scenarios - 10 điểm
- Component Integration Tests - 5 điểm
- Performance Testing - 5 điểm
- Output Sanitization Security - 2.5 điểm
- Part of overall 5 for common vulnerabilities

---

## File Cần Tạo/Edit

### Documentation Files
- `docs/THANH_RESPONSIBILITIES_EXPLAINED.md` ✓ (Created)
- `docs/DUC_RESPONSIBILITIES_EXPLAINED.md` ✓ (Created)
- `docs/PHAN_CONG_CHI_TIET.md` ✓ (Created)
- `docs/TEST_CASE_MATRIX_LOGIN.md` (Huy creates)
- `docs/TEST_CASE_MATRIX_PRODUCT.md` (Đức creates)

### Frontend Test Files
- `frontend/src/tests/validation.test.js` - Review (Thành)
- `frontend/src/tests/validateProduct.test.js` - Create (Nghĩa)
- `frontend/src/tests/LoginIntegration.test.jsx` - Extend (Thành)
- `frontend/src/tests/LoginMockExternal.test.jsx` - Create (Huy)
- `frontend/src/tests/ProductComponentsIntegration.test.jsx` - Extend (Đức)
- `frontend/src/tests/ProductMockExternal.test.jsx` - Create (Nghĩa)
- `frontend/src/tests/security/sanitization.test.js` - Create (Đức)
- `frontend/src/utils/sanitization.js` - Create (Đức, support file)

### Backend Test Files
- `backend/src/test/java/com/flogin/LoginServiceTest.java` - Create (Phát)
- `backend/src/test/java/com/flogin/LoginServiceMockTest.java` - Create (Phát)
- `backend/src/test/java/com/flogin/LoginControllerIntegrationTest.java` - Create (Huy)
- `backend/src/test/java/com/flogin/ProductServiceTest.java` - Create (Danh)
- `backend/src/test/java/com/flogin/ProductServiceMockTest.java` - Create (Danh)
- `backend/src/test/java/com/flogin/ProductControllerIntegrationTest.java` - Create (Danh)

### E2E Test Files
- `frontend/src/tests/cypress/e2e/login-scenarios.cy.js` - Create (Thành)
- `frontend/src/tests/cypress/e2e/product-e2e-scenarios.cy.js` - Create (Danh)
- `frontend/src/tests/cypress/pages/ProductPage.js` - Create (Danh)

### Performance & CI/CD Files
- `performance-tests/scripts/product-test.js` - Create/Extend (Đức)
- `.github/workflows/test.yml` - Create/Extend (Nghĩa, Phát)

---

## Key Metrics

| Metric | Target |
|--------|--------|
| Total Points | 91 |
| Core Points | 80 |
| Bonus Points | 15 |
| Test Case Count | 300+ |
| Code Coverage | >= 80% |
| Timeline | 4 weeks |

---

## Quick Links

- **Thành's Docs**: `docs/THANH_RESPONSIBILITIES_EXPLAINED.md`
- **Đức's Docs**: `docs/DUC_RESPONSIBILITIES_EXPLAINED.md`
- **Full Assignment**: `docs/PHAN_CONG_CHI_TIET.md`
- **Danh's Docs**: `docs/DANH_RESPONSIBILITIES_EXPLAINED.md` (existing)

---

## Contacts

- **Login Lead**: Huy
- **Product Lead**: Danh
- **Security Lead**: Đức
- **CI/CD Lead**: Phát
- **Overall PM**: (Assign if needed)

---

## Status Tracking

All 6 team members have detailed documentation and clear assignment of tasks.

Last Updated: December 4, 2025
