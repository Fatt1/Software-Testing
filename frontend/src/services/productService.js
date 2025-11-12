/**
 * Product Service - API calls cho quản lý sản phẩm
 */

/**
 * Get all products
 * @returns {Promise} - List of products
 */
export const getAllProducts = async () => {
  try {
    // In thực tế: await fetch('/api/products');
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          success: true,
          data: [
            { id: 1, name: 'Laptop', price: 1000, quantity: 5, category: 'Electronics', description: 'High performance laptop' },
            { id: 2, name: 'Mouse', price: 25, quantity: 50, category: 'Electronics', description: 'Wireless mouse for computer' }
          ]
        });
      }, 300);
    });
  } catch (error) {
    throw error;
  }
};

/**
 * Get product by ID
 * @param {string|number} id - Product ID
 * @returns {Promise} - Product details
 */
export const getProductById = async (id) => {
  try {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (id) {
          resolve({
            success: true,
            data: { id, name: 'Laptop', price: 1000, quantity: 5, category: 'Electronics', description: 'High performance laptop' }
          });
        } else {
          reject(new Error('Product not found'));
        }
      }, 300);
    });
  } catch (error) {
    throw error;
  }
};

/**
 * Create new product
 * @param {object} product - Product data
 * @returns {Promise} - Created product
 */
export const createProduct = async (product) => {
  try {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (product && product.name && product.price) {
          resolve({
            success: true,
            data: { id: Math.random(), ...product }
          });
        } else {
          reject(new Error('Invalid product data'));
        }
      }, 300);
    });
  } catch (error) {
    throw error;
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
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (id && product) {
          resolve({
            success: true,
            data: { id, ...product }
          });
        } else {
          reject(new Error('Invalid update data'));
        }
      }, 300);
    });
  } catch (error) {
    throw error;
  }
};

/**
 * Delete product
 * @param {string|number} id - Product ID
 * @returns {Promise} - Deletion result
 */
export const deleteProduct = async (id) => {
  try {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (id) {
          resolve({
            success: true,
            message: 'Product deleted successfully'
          });
        } else {
          reject(new Error('Invalid product ID'));
        }
      }, 300);
    });
  } catch (error) {
    throw error;
  }
};
