# Performance Testing với k6

## 📋 Mục lục

- [Setup](#setup)
- [Cách chạy tests](#cách-chạy-tests)
- [Kết quả phân tích](#kết-quả-phân-tích)
- [Recommendations](#recommendations)

---

## 🚀 Setup

### 1. Cài đặt k6

**Windows (PowerShell as Administrator):**

```powershell
# Dùng Chocolatey
choco install k6

# Hoặc download installer từ: https://k6.io/docs/get-started/installation/
```

**macOS:**

```bash
brew install k6
```

**Linux (Ubuntu/Debian):**

```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

### 2. Kiểm tra cài đặt

```bash
k6 version
```

### 3. Cấu trúc thư mục

```
performance-tests/
├── scripts/
│   ├── config.js              # Configuration chung
│   ├── login-test.js          # Performance test cho Login API
│   └── product-test.js        # Performance test cho Product API
├── results/                   # Thư mục chứa kết quả test
│   ├── login-100-users.json
│   ├── login-500-users.json
│   ├── login-1000-users.json
│   ├── login-stress-test.json
│   ├── product-100-users.json
│   ├── product-500-users.json
│   ├── product-1000-users.json
│   └── product-stress-test.json
└── README.md                  # File này
```

---

## 🏃 Cách chạy tests

### Trước khi chạy tests:

1. **Khởi động backend server:**

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Đảm bảo server đang chạy:**

   - URL: http://localhost:8080
   - Test endpoint: http://localhost:8080/api/auth/login

3. **Seed test data:**
   - Đảm bảo có 3 users trong database (đã tự động seed khi khởi động app)

### A) Login API Tests

#### 1. Load Test - 100 concurrent users (5 phút)

```bash
cd performance-tests/scripts

# Mở login-test.js, uncomment scenario load_100
# Comment các scenarios khác

k6 run login-test.js --out json=../results/login-100-users.json
```

#### 2. Load Test - 500 concurrent users (5 phút)

```bash
# Mở login-test.js, uncomment scenario load_500
# Comment các scenarios khác

k6 run login-test.js --out json=../results/login-500-users.json
```

#### 3. Load Test - 1000 concurrent users (5 phút)

```bash
# Mở login-test.js, uncomment scenario load_1000
# Comment các scenarios khác

k6 run login-test.js --out json=../results/login-1000-users.json
```

#### 4. Stress Test - Tìm breaking point

```bash
# Mở login-test.js, uncomment scenario stress_test
# Comment các scenarios khác

k6 run login-test.js --out json=../results/login-stress-test.json
```

### B) Product API Tests

#### 1. Load Test - 100 concurrent users

```bash
cd performance-tests/scripts

# Mở product-test.js, uncomment scenario load_100
k6 run product-test.js --out json=../results/product-100-users.json
```

#### 2. Load Test - 500 concurrent users

```bash
# Uncomment scenario load_500
k6 run product-test.js --out json=../results/product-500-users.json
```

#### 3. Load Test - 1000 concurrent users

```bash
# Uncomment scenario load_1000
k6 run product-test.js --out json=../results/product-1000-users.json
```

#### 4. Stress Test

```bash
# Uncomment scenario stress_test
k6 run product-test.js --out json=../results/product-stress-test.json
```

### C) Chạy với HTML Report (đẹp hơn)

```bash
# Cài đặt k6 HTML reporter
npm install -g k6-html-reporter

# Chạy test và tạo HTML report
k6 run login-test.js --out json=../results/login-100-users.json
k6-html-reporter ../results/login-100-users.json --output ../results/login-100-users.html

# Mở HTML report trong browser
```

---

## 📊 Kết quả phân tích

### 1. Login API Performance Results

#### Load Test - 100 concurrent users

**Expected Results:**

- ✅ **Success Rate:** > 99%
- ✅ **Average Response Time:** < 500ms
- ✅ **95th Percentile:** < 2000ms
- ✅ **99th Percentile:** < 3000ms
- ✅ **Throughput:** ~200 requests/second
- ✅ **Error Rate:** < 1%

**Actual Results:** (Điền sau khi chạy test)

```
Total Requests: _______
Successful: _______
Failed: _______
Average Response Time: _______ ms
p95: _______ ms
p99: _______ ms
Throughput: _______ req/s
Error Rate: _______ %
```

#### Load Test - 500 concurrent users

**Expected Results:**

- ✅ **Success Rate:** > 95%
- ⚠️ **Average Response Time:** < 1000ms
- ⚠️ **95th Percentile:** < 3000ms
- ⚠️ **99th Percentile:** < 5000ms
- ✅ **Throughput:** ~500 requests/second
- ✅ **Error Rate:** < 5%

**Actual Results:** (Điền sau khi chạy test)

```
Total Requests: _______
Successful: _______
Failed: _______
Average Response Time: _______ ms
p95: _______ ms
p99: _______ ms
Throughput: _______ req/s
Error Rate: _______ %
```

#### Load Test - 1000 concurrent users

**Expected Results:**

- ⚠️ **Success Rate:** > 90%
- ⚠️ **Average Response Time:** < 2000ms
- ⚠️ **95th Percentile:** < 5000ms
- ❌ **99th Percentile:** < 10000ms
- ⚠️ **Throughput:** ~800 requests/second
- ⚠️ **Error Rate:** < 10%

**Actual Results:** (Điền sau khi chạy test)

```
Total Requests: _______
Successful: _______
Failed: _______
Average Response Time: _______ ms
p95: _______ ms
p99: _______ ms
Throughput: _______ req/s
Error Rate: _______ %
```

#### Stress Test - Breaking Point

**Breaking Point Analysis:**

- **Breaking Point:** **\_\_\_** concurrent users
- **Error Rate starts increasing at:** **\_\_\_** users
- **Response time degradation at:** **\_\_\_** users
- **Server crash/timeout at:** **\_\_\_** users

---

### 2. Product API Performance Results

#### Mixed Operations (40% GET, 30% GET by ID, 15% CREATE, 10% UPDATE, 5% DELETE)

**Load Test - 100 users:**

```
Total Requests: _______
GET /products: _______ (Success Rate: _____%)
GET /products/{id}: _______ (Success Rate: _____%)
POST /products: _______ (Success Rate: _____%)
PUT /products/{id}: _______ (Success Rate: _____%)
DELETE /products/{id}: _______ (Success Rate: _____%)

Average Response Times:
- GET All: _______ ms
- GET by ID: _______ ms
- CREATE: _______ ms
- UPDATE: _______ ms
- DELETE: _______ ms

Overall Success Rate: _______ %
```

**Load Test - 500 users:**

```
(Điền kết quả tương tự)
```

**Load Test - 1000 users:**

```
(Điền kết quả tương tự)
```

---

## 💡 Recommendations

### 1. Performance Optimization

#### Database Level:

- ✅ **Add indexes** cho các columns thường query:

  ```sql
  CREATE INDEX idx_product_name ON products(product_name);
  CREATE INDEX idx_product_category ON products(category);
  CREATE INDEX idx_user_username ON users(user_name);
  CREATE INDEX idx_user_email ON users(email);
  ```

- ✅ **Connection Pool tuning:**
  ```properties
  # application.properties
  spring.datasource.hikari.maximum-pool-size=50
  spring.datasource.hikari.minimum-idle=10
  spring.datasource.hikari.connection-timeout=30000
  spring.datasource.hikari.idle-timeout=600000
  spring.datasource.hikari.max-lifetime=1800000
  ```

#### Application Level:

- ✅ **Enable caching** cho read-heavy operations:

  ```java
  @Cacheable("products")
  public ProductDto getProductById(long id) { ... }
  ```

- ✅ **Add pagination** cho all GET endpoints (✅ Đã implement)

- ✅ **Use async processing** cho non-critical operations:
  ```java
  @Async
  public CompletableFuture<Void> sendNotification(...) { ... }
  ```

#### Server Configuration:

- ✅ **Increase thread pool:**

  ```properties
  server.tomcat.threads.max=200
  server.tomcat.threads.min-spare=10
  server.tomcat.accept-count=100
  ```

- ✅ **Enable HTTP/2:**

  ```properties
  server.http2.enabled=true
  ```

- ✅ **Enable response compression:**
  ```properties
  server.compression.enabled=true
  server.compression.mime-types=application/json,text/html
  ```

### 2. Scalability Recommendations

#### Horizontal Scaling:

- ✅ **Load Balancer:** Nginx/HAProxy
- ✅ **Multiple app instances:** Docker + Kubernetes
- ✅ **Database replication:** Master-Slave setup

#### Vertical Scaling:

- ✅ **Increase server resources:**
  - CPU: 4+ cores
  - RAM: 8+ GB
  - SSD storage

#### Caching Strategy:

- ✅ **Redis** cho session và frequently accessed data
- ✅ **CDN** cho static resources
- ✅ **Database query cache**

### 3. Monitoring & Alerting

#### Metrics to Monitor:

- ✅ **Response time:** p50, p95, p99
- ✅ **Error rate:** < 1% (normal), < 5% (acceptable)
- ✅ **Throughput:** requests/second
- ✅ **CPU usage:** < 70% (normal), < 90% (warning)
- ✅ **Memory usage:** < 80%
- ✅ **Database connections:** active/idle ratio

#### Tools:

- **Prometheus + Grafana:** Real-time monitoring
- **ELK Stack:** Log aggregation and analysis
- **APM Tools:** New Relic, Datadog, AppDynamics

### 4. Security Considerations

- ✅ **Rate limiting:** Protect against DDoS

  ```java
  @RateLimiter(name = "loginApi", fallbackMethod = "rateLimitFallback")
  ```

- ✅ **Circuit breaker:** Prevent cascade failures
- ✅ **Request timeout:** Avoid long-running requests
- ✅ **Input validation:** Prevent SQL injection

### 5. Testing Strategy

#### Regular Performance Testing:

- ✅ **Weekly:** Quick smoke tests (100 users)
- ✅ **Monthly:** Full load tests (100, 500, 1000 users)
- ✅ **Quarterly:** Stress tests to find new breaking points
- ✅ **Before release:** Full performance regression testing

#### Continuous Monitoring:

- ✅ **Real-time alerts** when metrics exceed thresholds
- ✅ **Automated performance tests** in CI/CD pipeline
- ✅ **Trend analysis** to detect gradual degradation

---

## 📈 Performance Baseline

### Current System Capacity (Example):

| Metric                        | 100 Users | 500 Users | 1000 Users | Breaking Point |
| ----------------------------- | --------- | --------- | ---------- | -------------- |
| Login API - Avg Response      | 450ms     | 850ms     | 1800ms     | 2500 users     |
| Login API - p95               | 1200ms    | 2500ms    | 4500ms     | -              |
| Login API - Success Rate      | 99.5%     | 97%       | 92%        | 85% @ 2500     |
| Product GET - Avg Response    | 380ms     | 720ms     | 1500ms     | 3000 users     |
| Product CREATE - Avg Response | 620ms     | 1100ms    | 2300ms     | 2000 users     |
| Throughput (req/s)            | 200       | 550       | 850        | 1200 @ 2000    |

_(Điền số liệu thật sau khi chạy tests)_

---

## 🎯 Success Criteria

### Acceptable Performance:

- ✅ Login API: < 2s response time for 1000 concurrent users
- ✅ Product API: < 3s response time for 1000 concurrent users
- ✅ Success rate: > 95% under normal load
- ✅ Error rate: < 5% under stress

### Excellent Performance:

- ✅ Login API: < 1s response time for 1000 concurrent users
- ✅ Product API: < 2s response time for 1000 concurrent users
- ✅ Success rate: > 99% under normal load
- ✅ Error rate: < 1% under stress

---

## 📝 Notes

- Đảm bảo server đã warm-up trước khi chạy tests
- Chạy tests trong môi trường ổn định (không có background processes khác)
- Record system metrics (CPU, Memory, Network) trong khi test
- So sánh kết quả giữa các lần chạy để detect regressions
- Document bất kỳ issues nào phát hiện trong quá trình test

---

## 🔗 References

- k6 Documentation: https://k6.io/docs/
- Performance Testing Best Practices: https://k6.io/docs/testing-guides/
- Spring Boot Performance Tuning: https://spring.io/guides/
