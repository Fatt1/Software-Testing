export const BASE_URL = "http://localhost:6969";

export const TEST_USERS = [
  { userName: "admin", password: "admin123" },
  { userName: "user01", password: "password123" },
  { userName: "testuser", password: "test1234" },
];

export const THRESHOLDS = {
  // HTTP errors phải < 1%
  http_req_failed: ["rate<0.01"],

  // 95% requests phải hoàn thành trong 2s
  http_req_duration: ["p(95)<2000"],

  // 99% requests phải hoàn thành trong 3s
  "http_req_duration{expected_response:true}": ["p(99)<3000"],
};

export const SCENARIOS = {
  // Load test: 100 concurrent users
  load_100: {
    executor: "constant-vus",
    vus: 100,
    duration: "5m",
  },

  // Load test: 500 concurrent users
  load_500: {
    executor: "constant-vus",
    vus: 500,
    duration: "5m",
  },

  // Load test: 1000 concurrent users
  load_1000: {
    executor: "constant-vus",
    vus: 1000,
    duration: "5m",
  },

  // Stress test: Tìm breaking point
  stress_test: {
    executor: "ramping-vus",
    startVUs: 0,
    stages: [
      { duration: "2m", target: 100 }, // Tăng lên 100 users
      { duration: "5m", target: 100 }, // Giữ ở 100 users
      { duration: "2m", target: 500 }, // Tăng lên 500 users
      { duration: "5m", target: 500 }, // Giữ ở 500 users
      { duration: "2m", target: 1000 }, // Tăng lên 1000 users
      { duration: "5m", target: 1000 }, // Giữ ở 1000 users
      { duration: "2m", target: 2000 }, // Tăng lên 2000 users
      { duration: "5m", target: 2000 }, // Giữ ở 2000 users
      { duration: "2m", target: 0 }, // Giảm về 0
    ],
  },
};
