/**
 * Auth Service - API calls cho authentication
 */

/**
 * Login user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise} - Login result
 */
export const login = async (email, password) => {
  try {
    // In thực tế, đây sẽ gọi backend API
    // const response = await fetch('/api/login', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ email, password })
    // });
    // return response.json();
    
    // Mock for testing
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (email && password && email.includes('@')) {
          resolve({ 
            success: true, 
            message: 'Login successful',
            user: { email, id: '123' }
          });
        } else {
          reject(new Error('Invalid credentials'));
        }
      }, 500);
    });
  } catch (error) {
    throw error;
  }
};

/**
 * Logout user
 * @returns {Promise} - Logout result
 */
export const logout = async () => {
  try {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ success: true, message: 'Logged out successfully' });
      }, 300);
    });
  } catch (error) {
    throw error;
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
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (email && password && name && email.includes('@')) {
          resolve({ 
            success: true, 
            message: 'Registration successful',
            user: { email, name, id: '456' }
          });
        } else {
          reject(new Error('Invalid registration data'));
        }
      }, 500);
    });
  } catch (error) {
    throw error;
  }
};
