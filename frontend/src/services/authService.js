/**
 * Auth Service - API calls cho authentication
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
 * Login user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise} - Login result
 */
export const login = async (email, password) => {
  try {
    const response = await axiosInstance.post('/auth/login', {
      email,
      password
    });
    
    // Lưu token nếu có
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
    }
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Login failed');
  }
};


/**
 * Logout user
 * @returns {Promise} - Logout result
 */
export const logout = async () => {
  try {
    const response = await axiosInstance.post('/auth/logout');
    
    // Xóa token
    localStorage.removeItem('token');
    delete axiosInstance.defaults.headers.common['Authorization'];
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Logout failed');
  }
};

/**
 * Register new user
 * @param {string} email - User email
 * @param {string} password - User password
 * @param {string} name - User name
 * @returns {Promise} - Registration result
 */
export const register = async (email, password, name) => {
  try {
    const response = await axiosInstance.post('/auth/register', {
      email,
      password,
      name
    });
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Registration failed');
  }
};

/**
 * Set authorization token cho tất cả request
 * @param {string} token - JWT token
 */
export const setAuthToken = (token) => {
  if (token) {
    axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete axiosInstance.defaults.headers.common['Authorization'];
  }
};

export default axiosInstance;