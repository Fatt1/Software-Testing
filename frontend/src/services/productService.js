/**
 * Features:
 * - Complete CRUD operations
 * - Axios instance with custom configuration
 * - Environment-aware API URL
 * - Error handling with descriptive messages
 * - Pagination support (via getAllProducts)
 * - RESTful API design
 *
 * @module productService
 * @requires axios
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */

import axios from "axios";

/**
 * Get API base URL from environment variables
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
  return "https://swearingly-pseudocubic-beth.ngrok-free.dev/api";
};

/** @constant {string} API base URL for all product requests */
const API_BASE_URL = getApiUrl();

/**
 * Axios instance configured for product API calls
 *
 * @constant {AxiosInstance} axiosInstance
 * @private
 */
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    "ngrok-skip-browser-warning": "true",
  },
});

/**
 * Request interceptor to add JWT token to all requests
 * Retrieves token from localStorage and adds to Authorization header
 */
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * @async
 * @function getAllProducts
 * @param {number} page - Page number (default: 0)
 * @param {number} size - Items per page (default: 1000 to get all)
 * @returns {Promise<Object>} Products response object
 * @returns {Array<Object>} return.content - Array of product objects
 * @returns {number} return.totalElements - Total number of products
 * @returns {number} return.totalPages - Total number of pages
 * @returns {Object} return.pageable - Pagination information
 * @throws {Error} When API request fails
 */
export const getAllProducts = async (page = 0, size = 1000) => {
  try {
    const response = await axiosInstance.get(`/products?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    throw new Error(
      error.response?.data?.message || "Failed to fetch products"
    );
  }
};

/**
 * Retrieve single product by ID
 *
 * Fetches detailed information for a specific product.
 *
 * @async
 * @function getProductById
 * @param {string|number} id - Product ID to retrieve
 * @returns {Promise<Object>} Product details object
 * @returns {number} return.id - Product ID
 * @returns {string} return.productName - Product name
 * @returns {number} return.price - Product price
 * @returns {number} return.quantity - Available quantity
 * @returns {string} return.category - Product category
 * @returns {string} return.description - Product description
 * @throws {Error} When product not found or API request fails
 */
export const getProductById = async (id) => {
  try {
    const response = await axiosInstance.get(`/products/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Failed to fetch product");
  }
};

/**
 * Create new product
 * @async
 * @function createProduct
 * @param {Object} product - Product data object
 * @param {string} product.productName - Product name (required, unique)
 * @param {number} product.price - Product price (required, positive)
 * @param {number} product.quantity - Initial quantity (required, >= 0)
 * @param {string} product.category - Product category (required, valid enum)
 * @param {string} product.description - Product description (optional)
 * @returns {Promise<Object>} Created product with generated ID
 * @throws {Error} When validation fails or product name already exists
 */
export const createProduct = async (product) => {
  try {
    const response = await axiosInstance.post("/products", product);
    return response.data;
  } catch (error) {
    throw new Error(
      error.response?.data?.message || "Failed to create product"
    );
  }
};

/**
 * Update existing product
 *
 * Updates product information by ID.
 * All fields are required in update request.
 *
 * @async
 * @function updateProduct
 * @param {string|number} id - Product ID to update
 * @param {Object} product - Updated product data
 * @param {string} product.productName - Updated product name
 * @param {number} product.price - Updated price
 * @param {number} product.quantity - Updated quantity
 * @param {string} product.category - Updated category
 * @param {string} product.description - Updated description
 * @returns {Promise<Object>} Updated product object
 * @throws {Error} When product not found or validation fails
 */
export const updateProduct = async (id, product) => {
  try {
    const response = await axiosInstance.put(`/products/${id}`, product);
    return response.data;
  } catch (error) {
    throw new Error(
      error.response?.data?.message || "Failed to update product"
    );
  }
};

/**
 * Delete product by ID
 *
 * Permanently removes product from database.
 * This action cannot be undone.
 *
 * @async
 * @function deleteProduct
 * @param {string|number} id - Product ID to delete
 * @returns {Promise<void>} Empty response on success (204 No Content)
 * @throws {Error} When product not found or deletion fails
 */
export const deleteProduct = async (id) => {
  try {
    const response = await axiosInstance.delete(`/products/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(
      error.response?.data?.message || "Failed to delete product"
    );
  }
};

export default axiosInstance;
