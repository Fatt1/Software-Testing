import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";
import { BASE_URL, TEST_USERS } from "./config.js";

// Custom metrics
const loginSuccessRate = new Rate("login_success_rate");
const loginFailureRate = new Rate("login_failure_rate");
const loginDuration = new Trend("login_duration", true);
const totalRequests = new Counter("total_requests");
const successfulLogins = new Counter("successful_logins");
const failedLogins = new Counter("failed_logins");

// Chọn test scenario (uncomment để chọn)
export const options = {
  // LOAD TEST: 100 concurrent users
  scenarios: {
    load_100: {
      executor: "constant-vus",
      vus: 100,
      duration: "1m",
    },
  },

  // LOAD TEST: 500 concurrent users
  // scenarios: {
  //   load_500: {
  //     executor: "constant-vus",
  //     vus: 500,
  //     duration: "1m",
  //   },
  // },

  // // LOAD TEST: 1000 concurrent users
  // scenarios: {
  //   load_1000: {
  //     executor: "constant-vus",
  //     vus: 1000,
  //     duration: "1m",
  //   },
  // },

  // // STRESS TEST: Tìm breaking point
  // scenarios: {
  //   stress_test_step: {
  //     executor: "ramping-vus",
  //     startVUs: 0,
  //     stages: [
  //       // Khởi động nhẹ
  //       { duration: "30s", target: 100 },

  //       // Tăng tốc lên 1000
  //       { duration: "1m", target: 1000 },
  //       { duration: "2m", target: 1000 }, // Giữ 2p là đủ thấy lỗi rồi

  //       // Tăng tốc lên 3000
  //       { duration: "1m", target: 3000 },
  //       { duration: "2m", target: 3000 },

  //       // Đẩy lên cực hạn 5000
  //       { duration: "2m", target: 5000 }, // Tìm breaking point ở đoạn này
  //       { duration: "3m", target: 5000 }, // Chỉ giữ nếu chưa sập

  //       { duration: "1m", target: 0 },
  //     ],
  //   },
  // },

  thresholds: {
    http_req_failed: ["rate<0.01"], // < 1% errors
    http_req_duration: ["p(95)<2000", "p(99)<3000"],
    login_success_rate: ["rate>0.99"], // > 99% success
  },
};

export default function () {
  totalRequests.add(1);

  // Chọn ngẫu nhiên 1 user từ danh sách
  const user = TEST_USERS[Math.floor(Math.random() * TEST_USERS.length)];

  const loginPayload = JSON.stringify({
    userName: user.userName,
    password: user.password,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
    tags: { name: "LoginAPI" },
  };

  // Gửi login request
  const startTime = new Date();
  const response = http.post(
    `${BASE_URL}/api/auth/login`,
    loginPayload,
    params
  );
  const endTime = new Date();
  const duration = endTime - startTime;

  // Record custom metrics
  loginDuration.add(duration);

  // Kiểm tra response
  const loginSuccess = check(response, {
    "status is 200": (r) => r.status === 200,
    "has success field": (r) => {
      try {
        return JSON.parse(r.body).success !== undefined;
      } catch (e) {
        return false;
      }
    },
    "login successful": (r) => {
      try {
        return JSON.parse(r.body).success === true;
      } catch (e) {
        return false;
      }
    },
    "has token": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.token !== undefined && body.token !== null;
      } catch (e) {
        return false;
      }
    },
    "response time < 2000ms": (r) => r.timings.duration < 2000,
  });

  if (loginSuccess) {
    successfulLogins.add(1);
    loginSuccessRate.add(1);
  } else {
    failedLogins.add(1);
    loginFailureRate.add(1);
    loginSuccessRate.add(0);
  }

  // Simulate think time (người dùng thật không gửi request liên tục)
  sleep(Math.random() * 2 + 1); // Random 1-3 giây
}

// Setup function - chạy 1 lần trước khi test
export function setup() {
  console.log("=== Starting Login Performance Test ===");
  console.log(`Base URL: ${BASE_URL}`);
  console.log(`Test Users: ${TEST_USERS.length}`);

  // Warm-up request
  const warmup = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      userName: TEST_USERS[0].userName,
      password: TEST_USERS[0].password,
    }),
    {
      headers: { "Content-Type": "application/json" },
    }
  );

  if (warmup.status === 200) {
    console.log("✅ Server is ready - Warmup successful");
  } else {
    console.log("⚠️  Server warmup failed, but continuing test...");
  }
}

// Teardown function - chạy 1 lần sau khi test xong
export function teardown(data) {
  console.log("=== Login Performance Test Completed ===");
}
