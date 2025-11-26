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
 * Base URL for the API server under test
 * 
 * Configuration:
 * - Development: http://localhost:6969
 * - Staging: https://staging-api.example.com
 * - Production: https://api.example.com
 * 
 * Note: Ensure the server is running before executing performance tests.
 * The port 6969 is configured in backend application.properties.
 * 
 * @type {string}
 * @constant
 */
export const BASE_URL = "http://localhost:6969";

/**
 * Test user credentials for authentication testing
 * 
 * These users should exist in the database before running tests.
 * The backend automatically seeds these users on application startup.
 * 
 * User Roles:
 * - admin: Administrator with full permissions
 * - user01: Regular user with standard permissions
 * - testuser: Test user for validation scenarios
 * 
 * Security Note:
 * These are test credentials only. Never use production credentials
 * in performance tests to avoid security breaches.
 * 
 * Usage:
 * Tests randomly select users from this array to simulate
 * concurrent logins from different accounts.
 * 
 * @type {Array<{userName: string, password: string}>}
 * @constant
 */
export const TEST_USERS = [
  { userName: "admin", password: "admin123" },
  { userName: "user01", password: "password123" },
  { userName: "testuser", password: "test1234" },
];

/**
 * Performance Thresholds Configuration
 * 
 * Thresholds define pass/fail criteria for performance tests.
 * k6 will mark the test as failed if any threshold is breached.
 * 
 * Understanding Thresholds:
 * - rate<X: Percentage of requests must be below X (0.01 = 1%)
 * - p(95)<X: 95th percentile response time must be under X milliseconds
 * - p(99)<X: 99th percentile response time must be under X milliseconds
 * 
 * Why These Values?
 * - 1% error rate: Industry standard for acceptable failure rate
 * - 2000ms p95: Ensures 95% of users get response within 2 seconds
 * - 3000ms p99: Ensures 99% of users get response within 3 seconds
 * 
 * @type {Object}
 * @constant
 */
export const THRESHOLDS = {
  /**
   * HTTP Request Failure Rate
   * Maximum allowed failure rate: 1%
   * 
   * Failed requests include:
   * - Network errors (connection refused, timeout)
   * - HTTP 5xx server errors
   * - HTTP 4xx client errors (if not expected)
   */
  http_req_failed: ["rate<0.01"],

  /**
   * HTTP Request Duration - 95th Percentile
   * 95% of all requests must complete within 2000ms (2 seconds)
   * 
   * Target: <2000ms for good user experience
   * Warning: 2000-5000ms (acceptable but not ideal)
   * Critical: >5000ms (poor user experience)
   */
  http_req_duration: ["p(95)<2000"],

  /**
   * HTTP Request Duration - 99th Percentile (Successful Responses Only)
   * 99% of successful requests must complete within 3000ms (3 seconds)
   * 
   * Why p99 matters:
   * - Represents worst-case performance for most users
   * - Critical for SLA (Service Level Agreement) compliance
   */
  "http_req_duration{expected_response:true}": ["p(99)<3000"],
};

/**
 * Load Testing Scenarios Configuration
 * 
 * Defines different load testing patterns to evaluate system performance
 * under various conditions. Each scenario serves a specific testing purpose.
 * 
 * Executor Types:
 * - constant-vus: Maintains fixed number of virtual users
 * - ramping-vus: Gradually increases/decreases virtual users
 * 
 * @type {Object}
 * @constant
 */
export const SCENARIOS = {
  /**
   * Load Test: 100 Concurrent Users (Light Load)
   * 
   * Purpose:
   * - Baseline performance measurement
   * - Validate system works correctly under normal load
   * - Establish performance benchmarks
   * 
   * Expected Results:
   * - Success rate: >99%
   * - Average response time: <500ms
   * - p95 response time: <1000ms
   * - CPU usage: <50%
   * 
   * Use Case:
   * Simulates typical daily traffic for a small to medium application.
   * Example: 100 users actively using the system simultaneously.
   * 
   * Duration: 5 minutes
   * - First 30s: System warm-up and cache population
   * - Next 4min: Stable measurement period
   * - Last 30s: Cool-down (still measuring)
   * 
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
   * Load Test: 500 Concurrent Users (Medium Load)
   * 
   * Purpose:
   * - Test system performance under moderate stress
   * - Identify performance degradation patterns
   * - Validate auto-scaling configurations
   * 
   * Expected Results:
   * - Success rate: >95%
   * - Average response time: <1000ms
   * - p95 response time: <2000ms
   * - CPU usage: 50-70%
   * 
   * Use Case:
   * Simulates peak traffic during business hours or marketing campaigns.
   * Example: Product launch day with increased user activity.
   * 
   * Warning Signs:
   * - Response time >3000ms: Need optimization
   * - Error rate >5%: System struggling
   * - Memory continuously increasing: Possible memory leak
   * 
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
   * Load Test: 1000 Concurrent Users (Heavy Load)
   * 
   * Purpose:
   * - Test system capacity limits
   * - Identify bottlenecks under heavy load
   * - Validate disaster recovery procedures
   * 
   * Expected Results:
   * - Success rate: >90%
   * - Average response time: <2000ms
   * - p95 response time: <5000ms
   * - CPU usage: 70-90%
   * 
   * Use Case:
   * Simulates exceptional traffic scenarios like:
   * - Black Friday sales
   * - Viral social media event
   * - DDoS attack simulation
   * 
   * Critical Thresholds:
   * - Response time >10s: System overloaded
   * - Error rate >10%: System failing
   * - Database connections maxed: Need pool tuning
   * 
   * Note:
   * If system fails at this level, consider:
   * - Horizontal scaling (more servers)
   * - Caching layer (Redis)
   * - Database read replicas
   * - CDN for static content
   * 
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
   * Stress Test: Gradual Ramp to Find Breaking Point
   * 
   * Purpose:
   * - Identify system's maximum capacity (breaking point)
   * - Observe performance degradation pattern
   * - Test system recovery after stress
   * - Validate alerting and monitoring systems
   * 
   * Test Phases:
   * 
   * Phase 1: Initial Ramp (0-100 users, 2 min)
   * - System warm-up and cache population
   * 
   * Phase 2: Sustained Light Load (100 users, 5 min)
   * - Verify stable performance and monitor resource usage
   * 
   * Phase 3: Ramp to Medium Load (100-500 users, 2 min)
   * - Observe performance degradation patterns
   * 
   * Phase 4: Sustained Medium Load (500 users, 5 min)
   * - Measure performance under stress
   * 
   * Phase 5: Ramp to Heavy Load (500-1000 users, 2 min)
   * - Push system to limits
   * 
   * Phase 6: Sustained Heavy Load (1000 users, 5 min)
   * - Maximum sustainable load testing
   * 
   * Phase 7: Ramp to Breaking Point (1000-2000 users, 2 min)
   * - Find actual breaking point
   * 
   * Phase 8: Beyond Breaking Point (2000 users, 5 min)
   * - System behavior under overload
   * 
   * Phase 9: Cool Down (2000-0 users, 2 min)
   * - Test graceful degradation and recovery
   * 
   * Expected Outcomes:
   * - Breaking point: 1500-2000 users (typical)
   * - Error rate starts increasing: ~1200 users
   * - Response time >10s: ~1800 users
   * 
   * What to Monitor:
   * - Response time percentiles (p50, p95, p99)
   * - Error rate and error types
   * - CPU and memory usage
   * - Database connection pool utilization
   * - Network bandwidth usage
   * 
   * Total Duration: 32 minutes
   * Run during off-peak hours to avoid production impact
   * 
   * @property {string} executor - ramping-vus for gradual load increase
   * @property {number} startVUs - Starting with 0 users
   * @property {Array<{duration: string, target: number}>} stages - Load stages
   */
  stress_test: {
    executor: "ramping-vus",
    startVUs: 0,
    stages: [
      { duration: "2m", target: 100 },   // Warm-up: Gradually reach 100 users
      { duration: "5m", target: 100 },   // Sustain: Hold at 100 users for baseline
      { duration: "2m", target: 500 },   // Ramp: Increase to 500 users
      { duration: "5m", target: 500 },   // Sustain: Hold at 500 users
      { duration: "2m", target: 1000 },  // Ramp: Increase to 1000 users
      { duration: "5m", target: 1000 },  // Sustain: Hold at 1000 users
      { duration: "2m", target: 2000 },  // Ramp: Push to 2000 users (breaking point)
      { duration: "5m", target: 2000 },  // Sustain: Hold at 2000 users (observe failure)
      { duration: "2m", target: 0 },     // Cool-down: Ramp down to 0 (test recovery)
    ],
  },
};

/**
 * Usage Examples:
 * 
 * Import in test files:
 * ```javascript
 * import { BASE_URL, TEST_USERS, THRESHOLDS, SCENARIOS } from './config.js';
 * 
 * export const options = {
 *   scenarios: {
 *     load_test: SCENARIOS.load_100
 *   },
 *   thresholds: THRESHOLDS
 * };
 * ```
 * 
 * Run specific scenario:
 * ```bash
 * # Light load test
 * k6 run login-test.js
 * 
 * # Change scenario in test file, then run
 * k6 run product-test.js
 * ```
 */
