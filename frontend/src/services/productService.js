/**
 * Product Service - API calls cho quản lý sản phẩm
 */

import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Tạo instance axios
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

/**
 * Get all products
 * @returns {Promise} - List of products
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
 * Get product by ID
 * @param {string|number} id - Product ID
 * @returns {Promise} - Product details
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
 * @param {object} product - Product data
 * @returns {Promise} - Created product
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
 * Update product
 * @param {string|number} id - Product ID
 * @param {object} product - Updated product data
 * @returns {Promise} - Updated product
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
 * Delete product
 * @param {string|number} id - Product ID
 * @returns {Promise} - Deletion result
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