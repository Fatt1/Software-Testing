/**
 * Performance Test Configuration File
 *
 * This file contains all shared configuration for k6 performance tests.
 * Modify these values to match your testing environment and requirements.
 *
 * @author Performance Testing Team
 * @since November 2025
 */

/**
 * @type {string}
 * @constant
 */
export const BASE_URL = "http://localhost:6969";

/**
 * @type {Array<{userName: string, password: string}>}
 * @constant
 */
export const TEST_USERS = [
  { userName: "admin", password: "admin123" },
  { userName: "user01", password: "password123" },
  { userName: "testuser", password: "test1234" },
];

/**

 * @type {Object}
 * @constant
 */
export const THRESHOLDS = {
  http_req_failed: ["rate<0.01"],

  http_req_duration: ["p(95)<2000"],

  "http_req_duration{expected_response:true}": ["p(99)<3000"],
};

/**

 * @type {Object}
 * @constant
 */
export const SCENARIOS = {
  /**
   * @property {string} executor - constant-vus maintains fixed user count
   * @property {number} vus - Virtual Users (concurrent users)
   * @property {string} duration - Test duration in minutes
   */
  load_100: {
    executor: "constant-vus",
    vus: 100,
    duration: "5m",
  },

  /**
   * @property {string} executor - constant-vus maintains fixed user count
   * @property {number} vus - Virtual Users (concurrent users)
   * @property {string} duration - Test duration in minutes
   */
  load_500: {
    executor: "constant-vus",
    vus: 500,
    duration: "5m",
  },

  /**
   * @property {string} executor - constant-vus maintains fixed user count
   * @property {number} vus - Virtual Users (concurrent users)
   * @property {string} duration - Test duration in minutes
   */
  load_1000: {
    executor: "constant-vus",
    vus: 1000,
    duration: "5m",
  },

  /**
   * @property {string} executor - ramping-vus for gradual load increase
   * @property {number} startVUs - Starting with 0 users
   * @property {Array<{duration: string, target: number}>} stages - Load stages
   */
  stress_test: {
    executor: "ramping-vus",
    startVUs: 0,
    stages: [
      { duration: "2m", target: 100 }, // Warm-up: Gradually reach 100 users
      { duration: "5m", target: 100 }, // Sustain: Hold at 100 users for baseline
      { duration: "2m", target: 500 }, // Ramp: Increase to 500 users
      { duration: "5m", target: 500 }, // Sustain: Hold at 500 users
      { duration: "2m", target: 1000 }, // Ramp: Increase to 1000 users
      { duration: "5m", target: 1000 }, // Sustain: Hold at 1000 users
      { duration: "2m", target: 2000 }, // Ramp: Push to 2000 users (breaking point)
      { duration: "5m", target: 2000 }, // Sustain: Hold at 2000 users (observe failure)
      { duration: "2m", target: 0 }, // Cool-down: Ramp down to 0 (test recovery)
    ],
  },
};
