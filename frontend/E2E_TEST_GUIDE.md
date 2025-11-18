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
