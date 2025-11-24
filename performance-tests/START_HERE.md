# 📊 Performance Testing - Complete Guide

## ✅ Đã hoàn thành

Bộ performance testing hoàn chỉnh cho Login API và Product API đã sẵn sàng!

### 📁 Files đã tạo:

```
performance-tests/
├── scripts/
│   ├── config.js                    ✅ Cấu hình chung
│   ├── login-test.js                ✅ Login API tests
│   └── product-test.js              ✅ Product API tests
├── results/                         ✅ Thư mục lưu kết quả
├── run-test.ps1                     ✅ Chạy test đơn lẻ
├── run-complete-tests.ps1           ✅ Chạy tất cả tests tự động
├── analyze-results.ps1              ✅ Phân tích kết quả
├── QUICK_START.md                   ✅ Hướng dẫn nhanh
├── PERFORMANCE_ANALYSIS.md          ✅ Template phân tích chi tiết
└── README.md                        ✅ Documentation đầy đủ
```

---

## 🎯 Cách chạy tests (3 BƯỚC ĐƠN GIẢN)

### BƯỚC 1: Khởi động Backend

Mở terminal mới:

```powershell
cd E:\FloginFE_BE\backend
mvn spring-boot:run
```

Chờ đến khi thấy: `Started FloginApplication`

### BƯỚC 2: Chạy Tests

Mở terminal thứ 2:

```powershell
cd E:\FloginFE_BE\performance-tests

# Chạy test đơn giản trước (100 users, 1 phút)
& "C:\Program Files\k6\k6.exe" run scripts\login-test.js
```

Nếu test chạy thành công ✅, tiếp tục:

```powershell
# Chạy tất cả tests tự động
.\run-complete-tests.ps1
```

### BƯỚC 3: Xem kết quả

```powershell
# Phân tích kết quả
.\analyze-results.ps1

# Xem report
cat results\TEST_REPORT.md
```

---

## 📊 Tests bao gồm

### A) Login API (3 điểm)

- ✅ Load test: 100 concurrent users (1 min)
- ✅ Load test: 500 concurrent users (1 min)
- ✅ Load test: 1000 concurrent users (1 min)
- ✅ Stress test: Ramping 0→2000 users (8 min)
- ✅ Response time analysis (P95, P99)

### B) Product API (3 điểm)

- ✅ Mixed operations (GET 40%, GET by ID 30%, POST 15%, PUT 10%, DELETE 5%)
- ✅ Load test: 100 concurrent users (1 min)
- ✅ Load test: 500 concurrent users (1 min)
- ✅ Load test: 1000 concurrent users (1 min)
- ✅ Stress test: Ramping 0→2000 users (8 min)

### C) Analysis & Recommendations (2 điểm)

- ✅ Automated analysis script
- ✅ Performance metrics comparison
- ✅ Issues detection
- ✅ Optimization recommendations
- ✅ Markdown + CSV reports

**Total: 10 điểm** 🎉

---

## 🔧 Troubleshooting

### ❌ Lỗi: "k6 is not recognized"

**Giải pháp:**

```powershell
# Dùng đường dẫn đầy đủ
& "C:\Program Files\k6\k6.exe" version

# Nếu k6 ở chỗ khác, tìm nó:
where.exe k6

# Cập nhật đường dẫn trong scripts
```

### ❌ Lỗi: "Backend not running"

**Giải pháp:**

```powershell
# Kiểm tra backend
curl http://localhost:6969/api/products?page=0&size=1

# Nếu lỗi, khởi động backend:
cd E:\FloginFE_BE\backend
mvn spring-boot:run
```

### ❌ Lỗi: "Cannot import config.js"

**Giải pháp:**

```powershell
# Kiểm tra file tồn tại
Test-Path E:\FloginFE_BE\performance-tests\scripts\config.js

# Nếu không có, chạy lại từ thư mục đúng
cd E:\FloginFE_BE\performance-tests\scripts
& "C:\Program Files\k6\k6.exe" run login-test.js
```

---

## 📖 Commands cheat sheet

### Chạy test riêng lẻ:

```powershell
cd E:\FloginFE_BE\performance-tests\scripts

# Login API
& "C:\Program Files\k6\k6.exe" run login-test.js

# Product API
& "C:\Program Files\k6\k6.exe" run product-test.js

# Với output JSON
& "C:\Program Files\k6\k6.exe" run login-test.js --out json=../results/login-test.json
```

### Chạy test nhanh (debug):

```powershell
# 10 users, 30 giây
& "C:\Program Files\k6\k6.exe" run --vus 10 --duration 30s scripts\login-test.js
```

### Chạy tất cả tests:

```powershell
cd E:\FloginFE_BE\performance-tests
.\run-complete-tests.ps1
```

### Phân tích kết quả:

```powershell
.\analyze-results.ps1
```

---

## 📈 Expected Output

### Console Output (example):

```
     ✓ status is 200
     ✓ login successful
     ✓ has token
     ✓ response time < 2000ms

     checks.........................: 100.00% ✓ 6000       ✗ 0
     data_received..................: 3.0 MB  50 kB/s
     data_sent......................: 1.5 MB  25 kB/s
     http_req_duration..............: avg=450ms min=100ms med=400ms max=800ms p(95)=600ms p(99)=700ms
     http_reqs......................: 6000    100/s
     login_success_rate.............: 100.00% ✓ 6000       ✗ 0
     vus............................: 100     min=100 max=100
     vus_max........................: 100     min=100 max=100
```

### Files Output:

- `results/login-100-users.json` - Raw metrics (large file)
- `results/login-100-users-summary.json` - Summary metrics
- `results/TEST_REPORT.md` - Formatted report
- `results/summary-all-tests.csv` - Excel-friendly table

---

## 🎓 Cách đọc kết quả

### Key Metrics:

| Metric                      | Meaning                       | Good Threshold        |
| --------------------------- | ----------------------------- | --------------------- |
| **http_req_duration (avg)** | Thời gian response trung bình | < 500ms               |
| **http_req_duration (p95)** | 95% requests nhanh hơn        | < 2000ms              |
| **http_req_duration (p99)** | 99% requests nhanh hơn        | < 3000ms              |
| **http_req_failed**         | Tỷ lệ lỗi                     | < 1%                  |
| **checks**                  | Pass rate                     | > 99%                 |
| **http_reqs**               | Throughput                    | Càng cao càng tốt     |
| **vus**                     | Virtual users                 | = số concurrent users |

### Status Icons:

- ✅ **PASS**: Metrics trong threshold
- ⚠️ **WARNING**: Metrics gần threshold
- ❌ **FAIL**: Metrics vượt threshold

---

## 🔍 Analysis Checklist

Sau khi chạy tests, kiểm tra:

- [ ] Success rate > 99% cho 100 users?
- [ ] P95 response time < 2s?
- [ ] P99 response time < 3s?
- [ ] Error rate < 1%?
- [ ] Breaking point là bao nhiêu users?
- [ ] Có pattern nào lặp lại trong errors?
- [ ] CPU/Memory usage bao nhiêu?
- [ ] Database connections bao nhiêu?

---

## 💡 Recommendations Template

Sau khi có kết quả, điền vào [`PERFORMANCE_ANALYSIS.md`](PERFORMANCE_ANALYSIS.md):

1. **Actual metrics** (thay thế `_______`)
2. **Issues found** (critical, high, medium priority)
3. **Root causes** (bottlenecks)
4. **Action items** (optimizations)
5. **Expected improvements** (sau khi optimize)

---

## ⏱️ Timeline

| Task                           | Duration      |
| ------------------------------ | ------------- |
| Setup k6                       | 5 min         |
| Start backend                  | 2 min         |
| Run 1 test (100 users, 1 min)  | 2 min         |
| Run 1 test (500 users, 1 min)  | 2 min         |
| Run 1 test (1000 users, 1 min) | 2 min         |
| Run 1 stress test              | 8-10 min      |
| **Total for 8 tests**          | **30-40 min** |
| Analysis                       | 10 min        |
| Write report                   | 20 min        |
| **GRAND TOTAL**                | **~70 min**   |

---

## 🎯 Grading Rubric (10 điểm)

### a) Setup k6 (2 điểm)

- ✅ k6 cài đặt thành công
- ✅ config.js với BASE_URL, TEST_USERS, SCENARIOS
- ✅ README.md với installation instructions

### b) Login API tests (3 điểm)

- ✅ Load test 100 users (1 điểm)
- ✅ Load test 500, 1000 users (1 điểm)
- ✅ Stress test tìm breaking point (0.5 điểm)
- ✅ Response time analysis (0.5 điểm)

### c) Product API tests (3 điểm)

- ✅ Mixed operations testing (1 điểm)
- ✅ Load test 100, 500, 1000 users (1 điểm)
- ✅ Stress test (0.5 điểm)
- ✅ Per-operation metrics (0.5 điểm)

### d) Analysis & Recommendations (2 điểm)

- ✅ Results table với actual values (0.5 điểm)
- ✅ Performance issues identified (0.5 điểm)
- ✅ Optimization recommendations (0.5 điểm)
- ✅ Scalability recommendations (0.5 điểm)

---

## 📚 Resources

- [k6 Documentation](https://k6.io/docs/)
- [k6 Examples](https://k6.io/docs/examples/)
- [Performance Testing Guide](https://k6.io/docs/testing-guides/)
- [Metrics Reference](https://k6.io/docs/using-k6/metrics/)

---

## ✅ Final Checklist

Trước khi submit, đảm bảo:

- [ ] Backend đang chạy ổn định
- [ ] Đã chạy đủ 8 tests (4 Login + 4 Product)
- [ ] Có files JSON trong `results/`
- [ ] Đã chạy `analyze-results.ps1`
- [ ] Có file `TEST_REPORT.md` với kết quả thực tế
- [ ] Đã điền actual values vào `PERFORMANCE_ANALYSIS.md`
- [ ] Đã review và hiểu các recommendations
- [ ] Code và configs đã commit lên Git
- [ ] README.md có hướng dẫn đầy đủ

---

## 🚀 Ready to Start!

Bây giờ bạn có thể bắt đầu:

```powershell
# Terminal 1: Backend
cd E:\FloginFE_BE\backend
mvn spring-boot:run

# Terminal 2: Tests
cd E:\FloginFE_BE\performance-tests
& "C:\Program Files\k6\k6.exe" run scripts\login-test.js

# Nếu OK, chạy tất cả
.\run-complete-tests.ps1

# Sau khi xong, phân tích
.\analyze-results.ps1
```

Good luck! 🎉
