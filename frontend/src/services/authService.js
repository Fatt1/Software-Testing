/**
 * @module authService
 * @requires axios
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */

import axios from "axios";

/**
 * @function getApiUrl
 * @returns {string} API base URL
 * @private
 */
const getApiUrl = () => {
  // Cho Jest/Node environment (testing)
  if (
    typeof process !== "undefined" &&
    process.env &&
    process.env.REACT_APP_API_URL
  ) {
    return process.env.REACT_APP_API_URL;
  }
  // Cho Vite environment (phía client - browser)
  if (typeof window !== "undefined") {
    return "https://swearingly-pseudocubic-beth.ngrok-free.dev/api";
  }
  // Fallback default URL
  return "https://swearingly-pseudocubic-beth.ngrok-free.dev/api";
};

/** @constant {string} API base URL for all authentication requests */
const API_BASE_URL = getApiUrl();

/**
 * @constant {AxiosInstance} axiosInstance
 * @private
 */
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Authenticate user with username and password
 * Flow:
 * 1. Send POST request to /auth/login with credentials
 * 2. Receive response with token and user data
 * 3. Store token in localStorage for persistence
 * 4. Set Authorization header for subsequent requests
 * 5. Return response data to caller
 *
 * @async
 * @function login
 * @param {string} username - User's username (min 3 characters)
 * @param {string} password - User's password (min 6 characters)
 * @returns {Promise<Object>} Login response object
 * @returns {boolean} return.success - Whether login was successful
 * @returns {string} return.token - JWT authentication token
 * @returns {Object} return.user - User information
 * @returns {string} return.message - Success/error message
 * @throws {Error} When login fails (invalid credentials, network error, etc.)
 *
 * @example
 */
export const login = async (username, password) => {
  try {
    // Send login request to backend API
    const response = await axiosInstance.post("/auth/login", {
      userName: username,
      password: password,
    });

    // Lưu token vào localStorage nếu authentication thành công
    if (response.data.token) {
      localStorage.setItem("token", response.data.token);
      // Set Authorization header cho tất cả requests tiếp theo
      axiosInstance.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${response.data.token}`;
    }

    return response.data;
  } catch (error) {
    // Extract error message from response or use default
    throw new Error(error.response?.data?.message || "Login failed");
  }
};

/**
 * Logout user
 * @returns {Promise} - Logout result
 */
export const logout = async () => {
  try {
    const response = await axiosInstance.post("/auth/logout");

    // Xóa token
    localStorage.removeItem("token");
    delete axiosInstance.defaults.headers.common["Authorization"];

    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Logout failed");
  }
};

/**
 * Register new user
 * @param {string} username - Username
 * @param {string} password - User password
 * @param {string} name - User name
 * @returns {Promise} - Registration result
 */
export const register = async (username, password, name) => {
  try {
    const response = await axiosInstance.post("/auth/register", {
      userName: username,
      password,
      name,
    });

    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Registration failed");
  }
};

/**
 * Set authorization token cho tất cả request
 * @param {string} token - JWT token
 */
export const setAuthToken = (token) => {
  if (token) {
    axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete axiosInstance.defaults.headers.common["Authorization"];
  }
};

export default axiosInstance;
