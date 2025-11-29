// ==================== API Configuration ====================
export const BASE_URL = "http://localhost:6969";

export const API_ENDPOINTS = {
  LOGIN: `${BASE_URL}/api/auth/login`,
  PRODUCTS: `${BASE_URL}/api/products`,
  PRODUCT_BY_ID: (id) => `${BASE_URL}/api/products/${id}`,
};

// ==================== Test Data ====================
/**
 * @type {Array<{userName: string, password: string}>}
 * @constant
 */
export const TEST_USERS = [
  { userName: "admin", password: "admin123" },
  { userName: "user01", password: "password123" },
  { userName: "testuser", password: "test1234" },
];

// ==================== HTTP Configuration ====================
export const HTTP_HEADERS = {
  JSON: {
    "Content-Type": "application/json",
  },
  WITH_TOKEN: (token) => ({
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  }),
};

export const HTTP_PARAMS = {
  timeout: "30s",
  tags: { name: "PerformanceTest" },
};

// ==================== Performance Thresholds ====================
/**
 * @type {Object}
 * @constant
 */
export const THRESHOLDS = {
  // Error rate phải < 1%
  http_req_failed: ["rate<0.01"],

  // Response time thresholds
  http_req_duration: ["p(95)<2000", "p(99)<3000"],

  // Login specific thresholds
  login_success_rate: ["rate>0.99"], // > 99% success
  login_duration: ["p(95)<1000", "p(99)<2000"],
};

/**
 * @type {Object}
 * @constant
 */
export const SCENARIOS = {
  // Load Test: 100 concurrent users
  load_100: {
    executor: "constant-vus",
    vus: 100,
    duration: "1m",
  },

  // Load Test: 500 concurrent users
  load_500: {
    executor: "constant-vus",
    vus: 500,
    duration: "1m",
  },

  // Load Test: 1000 concurrent users
  load_1000: {
    executor: "constant-vus",
    vus: 1000,
    duration: "1m",
  },

  // Stress Test: Find breaking point
  stress_test: {
    executor: "ramping-vus",
    startVUs: 0,
    stages: [
      { duration: "30s", target: 100 }, // Warm-up
      { duration: "1m", target: 1000 }, // Ramp to 1000
      { duration: "2m", target: 1000 }, // Hold at 1000
      { duration: "1m", target: 3000 }, // Ramp to 3000
      { duration: "2m", target: 3000 }, // Hold at 3000
      { duration: "2m", target: 5000 }, // Push to breaking point
      { duration: "3m", target: 5000 }, // Hold at breaking point
      { duration: "1m", target: 0 }, // Cool down
    ],
  },
};

// ==================== Test Configuration Settings ====================
export const TEST_CONFIG = {
  // Think time: simulate real user behavior (seconds)
  THINK_TIME_MIN: 1,
  THINK_TIME_MAX: 3,

  // Timeout settings
  REQUEST_TIMEOUT: "30s",

  // Retry settings
  MAX_RETRIES: 3,
  RETRY_DELAY: 1000, // milliseconds
};

// ==================== Helper Functions ====================
/**
 * Get random user from TEST_USERS
 * @returns {{userName: string, password: string}}
 */
export function getRandomUser() {
  return TEST_USERS[Math.floor(Math.random() * TEST_USERS.length)];
}

/**
 * Get random think time between min and max
 * @returns {number} Random seconds
 */
export function getRandomThinkTime() {
  return (
    Math.random() * (TEST_CONFIG.THINK_TIME_MAX - TEST_CONFIG.THINK_TIME_MIN) +
    TEST_CONFIG.THINK_TIME_MIN
  );
}

/**
 * Create login payload
 * @param {string} userName
 * @param {string} password
 * @returns {string} JSON string
 */
export function createLoginPayload(userName, password) {
  return JSON.stringify({ userName, password });
}
