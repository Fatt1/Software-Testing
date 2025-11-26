/**
 * Product Service Module - Product Management API Client
 * 
 * This module handles all product-related API calls including
 * CRUD operations (Create, Read, Update, Delete) for products.
 * 
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

import axios from 'axios';

/**
 * Get API base URL from environment variables
 * 
 * Supports both Jest/Node (testing) and Vite/browser (production) environments.
 * 
 * @function getApiUrl
 * @returns {string} API base URL
 * @private
 */
const getApiUrl = () => {
  // Cho Jest/Node environment (testing)
  if (typeof process !== 'undefined' && process.env && process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }
  // Cho Vite environment (phía client - browser)
  if (typeof window !== 'undefined') {
    return 'https://swearingly-pseudocubic-beth.ngrok-free.dev/api';
  }
  return 'https://swearingly-pseudocubic-beth.ngrok-free.dev/api';
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
    'Content-Type': 'application/json'
  }
});

/**
 * Retrieve all products from API
 * 
 * Fetches complete product list with pagination support.
 * Returns paginated data including total count and page info.
 * 
 * @async
 * @function getAllProducts
 * @returns {Promise<Object>} Products response object
 * @returns {Array<Object>} return.content - Array of product objects
 * @returns {number} return.totalElements - Total number of products
 * @returns {number} return.totalPages - Total number of pages
 * @returns {Object} return.pageable - Pagination information
 * @throws {Error} When API request fails
 * 
 * @example
 * const products = await getAllProducts();
 * console.log(products.content); // Array of products
 * console.log(products.totalElements); // Total count
 */
export const getAllProducts = async () => {
  try {
    const response = await axiosInstance.get('/products');
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to fetch products');
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
 * 
 * @example
 * const product = await getProductById(1);
 * console.log(product.productName);
 */
export const getProductById = async (id) => {
  try {
    const response = await axiosInstance.get(`/products/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to fetch product');
  }
};

/**
 * Create new product
 * 
 * Sends product data to API to create new product entry.
 * Validates data on server side before creation.
 * 
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
 * 
 * @example
 * const newProduct = await createProduct({
 *   productName: 'iPhone 15',
 *   price: 999.99,
 *   quantity: 50,
 *   category: 'Electronics',
 *   description: 'Latest model'
 * });
 */
export const createProduct = async (product) => {
  try {
    const response = await axiosInstance.post('/products', product);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to create product');
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
 * 
 * @example
 * const updated = await updateProduct(1, {
 *   productName: 'iPhone 15 Pro',
 *   price: 1099.99,
 *   quantity: 30,
 *   category: 'Electronics',
 *   description: 'Pro model'
 * });
 */
export const updateProduct = async (id, product) => {
  try {
    const response = await axiosInstance.put(`/products/${id}`, product);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to update product');
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
 * 
 * @example
 * await deleteProduct(1);
 * console.log('Product deleted successfully');
 */
export const deleteProduct = async (id) => {
  try {
    const response = await axiosInstance.delete(`/products/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to delete product');
  }
};

export default axiosInstance;