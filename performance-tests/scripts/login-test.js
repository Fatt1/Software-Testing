import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";
import {
  API_ENDPOINTS,
  HTTP_HEADERS,
  THRESHOLDS,
  SCENARIOS,
  getRandomUser,
  getRandomThinkTime,
  createLoginPayload,
} from "./config.js";

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
    load_100: SCENARIOS.load_100,
  },

  // LOAD TEST: 500 concurrent users
  // scenarios: {
  //   load_500: SCENARIOS.load_500,
  // },

  // LOAD TEST: 1000 concurrent users
  // scenarios: {
  //   load_1000: SCENARIOS.load_1000,
  // },

  // STRESS TEST: Tìm breaking point
  // scenarios: {
  //   stress_test: SCENARIOS.stress_test,
  // },

  thresholds: THRESHOLDS,
};

export default function () {
  totalRequests.add(1);

  // Lấy random user từ config
  const user = getRandomUser();

  // Tạo login payload từ config helper
  const loginPayload = createLoginPayload(user.userName, user.password);

  const params = {
    headers: HTTP_HEADERS.JSON,
    tags: { name: "LoginAPI" },
  };

  // Gửi login request đến endpoint từ config
  const startTime = new Date();
  const response = http.post(API_ENDPOINTS.LOGIN, loginPayload, params);
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
  sleep(getRandomThinkTime());
}

// Setup function - chạy 1 lần trước khi test
export function setup() {
  console.log("=== Starting Login Performance Test ===");
  console.log(`API Endpoint: ${API_ENDPOINTS.LOGIN}`);

  // Warm-up request
  const firstUser = getRandomUser();
  const warmup = http.post(
    API_ENDPOINTS.LOGIN,
    createLoginPayload(firstUser.userName, firstUser.password),
    { headers: HTTP_HEADERS.JSON }
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
