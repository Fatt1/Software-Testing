# 🧪 Hướng Dẫn Chạy Test Frontend

## 📋 Yêu Cầu Hệ Thống

- **Node.js**: v16.0.0 hoặc cao hơn
- **npm**: v7.0.0 hoặc cao hơn

## 📦 Thư Viện Cần Thiết

### Danh Sách Thư Viện (Dependencies)

| Thư Viện | Phiên Bản | Mục Đích |
|---------|---------|---------|
| **jest** | ^30.2.0 | Test runner chính |
| **@testing-library/react** | ^16.3.0 | Test React components |
| **@testing-library/jest-dom** | ^6.9.1 | Jest matchers cho DOM |
| **@testing-library/user-event** | ^14.6.1 | Mô phỏng tương tác người dùng |
| **babel-jest** | ^30.2.0 | Transpile JSX trong test |
| **jest-environment-jsdom** | ^30.2.0 | DOM environment cho Jest |
| **identity-obj-proxy** | ^3.0.0 | Mock CSS modules |

### Danh Sách Build Tools

| Công Cụ | Phiên Bản | Mục Đích |
|--------|---------|---------|
| **@babel/preset-env** | ^7.28.5 | Babel preset cho ES modules |
| **@babel/preset-react** | ^7.28.5 | Babel preset cho JSX |
| **vite** | ^7.1.7 | Development server & build tool |

## 🚀 Cài Đặt & Chạy Test

### Bước 1: Cài Đặt Dependencies

```bash
cd frontend
npm install
```

Câu lệnh này sẽ cài đặt tất cả thư viện được liệt kê trong `package.json`

### Bước 2: Chạy Test

**Chạy tất cả test:**
```bash
npm test
```

**Chạy test ở chế độ watch (tự động chạy lại khi code thay đổi):**
```bash
npm run test:watch
```

**Chạy test và sinh ra coverage report:**
```bash
npm run test:coverage
```

## 📊 Coverage Report

Sau khi chạy `npm run test:coverage`, báo cáo chi tiết sẽ được lưu tại:
```
frontend/coverage/lcov-report/index.html
```

Mở file này trong trình duyệt để xem báo cáo chi tiết.

## ✅ Các File Test

| File Test | Mục Đích |
|----------|---------|
| `src/tests/validateProduct.test.js` | Test hàm validate sản phẩm |
| `src/tests/validation.test.js` | Test hàm validate người dùng |
| `src/tests/LoginIntegration.test.jsx` | Test tích hợp form login |
| `src/tests/LoginMockExternal.test.jsx` | Test login với mock API |
| `src/tests/ProductComponentsIntegration.test.jsx` | Test tích hợp quản lý sản phẩm |
| `src/tests/ProductMockExternal.test.jsx` | Test sản phẩm với mock API |

## 📈 Kết Quả Test Hiện Tại

```
Test Suites: 6 passed, 6 total
Tests:       184 passed, 184 total
Coverage:    51.43% (Statements)
```

## 🛠️ Các Lệnh Hữu Ích

```bash
# Xóa cache của jest
npm test -- --clearCache

# Chạy một file test cụ thể
npm test -- validateProduct.test.js

# Chạy test với verbose output
npm test -- --verbose

# Chạy test và dừng lại ở test đầu tiên bị lỗi
npm test -- --bail
```

## 🐛 Troubleshooting

### Lỗi: "npm: command not found"
- **Giải pháp**: Cài đặt Node.js từ https://nodejs.org/

### Lỗi: "Cannot find module '@testing-library/react'"
- **Giải pháp**: Chạy `npm install` để cài đặt dependencies

### Lỗi: Jest timeout
- **Giải pháp**: Chạy lệnh: `npm test -- --testTimeout=20000`

## 📝 Ghi Chú

- Tất cả thư viện test đã được cấu hình trong `jest.config.cjs` và `jest.setup.cjs`
- Babel được cấu hình trong `babel.config.cjs` để transpile JSX
- CSS modules được mock qua `identity-obj-proxy`

---

**Để biết thêm thông tin**, vui lòng xem `package.json` trong folder `frontend`
