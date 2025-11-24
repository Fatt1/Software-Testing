# 🚀 Hướng dẫn chạy Performance Tests

## Bước 1: Khởi động Backend

Mở terminal mới và chạy:

```powershell
cd E:\FloginFE_BE\backend
mvn spring-boot:run
```

Đợi đến khi thấy: `Started FloginApplication in X seconds`

## Bước 2: Chạy Performance Tests

### Option A: Chạy từng test riêng lẻ

```powershell
cd E:\FloginFE_BE\performance-tests\scripts

# Test Login API - 100 users
& "C:\Program Files\k6\k6.exe" run login-test.js --out json=../results/login-100-users.json --summary-export=../results/login-100-users-summary.json

# Đợi test xong, sau đó sửa file login-test.js:
# - Comment scenarios load_100
# - Uncomment scenarios load_500
# Rồi chạy:

& "C:\Program Files\k6\k6.exe" run login-test.js --out json=../results/login-500-users.json --summary-export=../results/login-500-users-summary.json

# Tương tự cho 1000 users và stress test
```

### Option B: Chạy tất cả tests tự động (KHUYẾN NGHỊ)

```powershell
cd E:\FloginFE_BE\performance-tests
.\run-complete-tests.ps1
```

Script sẽ tự động:

- ✅ Tìm k6 executable
- ✅ Kiểm tra backend đang chạy
- ✅ Chạy 8 tests (4 Login + 4 Product)
- ✅ Lưu kết quả vào folder `results/`
- ✅ Tạo summary JSON cho mỗi test

## Bước 3: Phân tích kết quả

```powershell
cd E:\FloginFE_BE\performance-tests
.\analyze-results.ps1
```

Script sẽ:

- ✅ Parse tất cả summary JSON files
- ✅ Tạo bảng so sánh
- ✅ Export CSV: `results/summary-all-tests.csv`
- ✅ Tạo report: `results/TEST_REPORT.md`
- ✅ Phát hiện issues và đưa ra recommendations

## Bước 4: Xem kết quả

```powershell
# Xem summary table
cat results\TEST_REPORT.md

# Xem CSV trong Excel
start results\summary-all-tests.csv

# Xem raw k6 output
cat results\login-100-users.json
```

## 📊 Output Files

Sau khi chạy xong, bạn sẽ có:

```
results/
├── login-100-users.json              # Raw metrics
├── login-100-users-summary.json      # Summary data
├── login-500-users.json
├── login-500-users-summary.json
├── login-1000-users.json
├── login-1000-users-summary.json
├── login-stress-test.json
├── login-stress-test-summary.json
├── product-100-users.json
├── product-100-users-summary.json
├── product-500-users.json
├── product-500-users-summary.json
├── product-1000-users.json
├── product-1000-users-summary.json
├── product-stress-test.json
├── product-stress-test-summary.json
├── summary-all-tests.csv             # Excel-friendly summary
└── TEST_REPORT.md                    # Markdown report
```

## 🐛 Troubleshooting

### Lỗi: "k6 is not recognized"

```powershell
# Dùng đường dẫn đầy đủ
& "C:\Program Files\k6\k6.exe" version

# Hoặc thêm alias
Set-Alias -Name k6 -Value "C:\Program Files\k6\k6.exe"
```

### Lỗi: "Backend not running"

```powershell
# Kiểm tra backend
curl http://localhost:6969/api/products?page=0&size=1

# Nếu lỗi, start backend:
cd E:\FloginFE_BE\backend
mvn spring-boot:run
```

### Lỗi: "Cannot import config.js"

Đảm bảo file `scripts/config.js` tồn tại và có nội dung đúng.

## ⏱️ Thời gian ước tính

- **1 test (100 users, 1 min):** ~1-2 phút
- **1 test (500 users, 1 min):** ~1-2 phút
- **1 test (1000 users, 1 min):** ~1-2 phút
- **1 stress test:** ~8-10 phút
- **Tất cả 8 tests:** ~30-40 phút

## 📝 Tips

1. **Chạy test nhẹ trước** (100 users) để đảm bảo setup đúng
2. **Monitor backend logs** để phát hiện lỗi sớm
3. **Đóng các ứng dụng khác** để kết quả chính xác hơn
4. **Chạy nhiều lần** và lấy average để kết quả ổn định
5. **Backup kết quả** trước khi chạy lại tests

## 🎯 Mục tiêu

### Load Tests

- ✅ **100 users:** Success rate > 99%, P95 < 2s
- ⚠️ **500 users:** Success rate > 95%, P95 < 3s
- ⚠️ **1000 users:** Success rate > 90%, P95 < 5s

### Stress Test

- 🔍 Tìm breaking point (hệ thống bắt đầu fail)
- 📊 Phân tích degradation pattern
- 💡 Xác định bottlenecks

## ✅ Checklist

- [ ] Backend đã chạy và sẵn sàng
- [ ] k6 đã cài đặt và test được
- [ ] Đã đọc hướng dẫn này
- [ ] Đã chạy test nhẹ (100 users) thành công
- [ ] Đã chạy tất cả 8 tests
- [ ] Đã phân tích kết quả với analyze-results.ps1
- [ ] Đã review TEST_REPORT.md
- [ ] Đã điền kết quả vào PERFORMANCE_ANALYSIS.md
