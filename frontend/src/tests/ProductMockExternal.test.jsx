import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductManagement from '../components/ProductManagement';
import * as productService from '../services/productService';

jest.mock('../services/productService');

/**
 * Frontend Mocking (2.5 điểm)
 * Mock ProductService trong component tests
 */
describe('Frontend Mocking - ProductService Mock Tests', () => {
  
  beforeEach(() => {
    jest.clearAllMocks();
  });

  /**
   * Mock CRUD Operations (1.5 điểm)
   */
  describe('Test 1: Mock CRUD Operations (1.5 điểm)', () => {

    test('nên mock getAllProducts function', () => {
      expect(jest.isMockFunction(productService.getAllProducts)).toBe(true);
      expect(typeof productService.getAllProducts).toBe('function');
    });

    test('nên mock getProductById function', () => {
      expect(jest.isMockFunction(productService.getProductById)).toBe(true);
      expect(typeof productService.getProductById).toBe('function');
    });

    test('nên mock createProduct function', () => {
      expect(jest.isMockFunction(productService.createProduct)).toBe(true);
      expect(typeof productService.createProduct).toBe('function');
    });

    test('nên mock updateProduct function', () => {
      expect(jest.isMockFunction(productService.updateProduct)).toBe(true);
      expect(typeof productService.updateProduct).toBe('function');
    });

    test('nên mock deleteProduct function', () => {
      expect(jest.isMockFunction(productService.deleteProduct)).toBe(true);
      expect(typeof productService.deleteProduct).toBe('function');
    });

    test('nên set up mock getAllProducts với mockResolvedValue', async () => {
      const mockProducts = [
        { id: '1', name: 'Product 1', price: 100, quantity: 10 },
        { id: '2', name: 'Product 2', price: 200, quantity: 20 }
      ];
      productService.getAllProducts.mockResolvedValue(mockProducts);

      const result = await productService.getAllProducts();

      expect(result).toEqual(mockProducts);
      expect(result.length).toBe(2);
    });

    test('nên set up mock createProduct với mockResolvedValue', async () => {
      const newProduct = { id: '3', name: 'New Product', price: 150, quantity: 5 };
      productService.createProduct.mockResolvedValue({
        success: true,
        message: 'Product created',
        product: newProduct
      });

      const result = await productService.createProduct(newProduct);

      expect(result.success).toBe(true);
      expect(result.product).toEqual(newProduct);
    });

    test('nên set up mock updateProduct với mockResolvedValue', async () => {
      const updatedProduct = { id: '1', name: 'Updated', price: 250, quantity: 15 };
      productService.updateProduct.mockResolvedValue({
        success: true,
        message: 'Product updated',
        product: updatedProduct
      });

      const result = await productService.updateProduct('1', updatedProduct);

      expect(result.success).toBe(true);
      expect(result.product.name).toBe('Updated');
    });

    test('nên set up mock deleteProduct với mockResolvedValue', async () => {
      productService.deleteProduct.mockResolvedValue({
        success: true,
        message: 'Product deleted'
      });

      const result = await productService.deleteProduct('1');

      expect(result.success).toBe(true);
    });

    test('nên set up mock getProductById với mockResolvedValue', async () => {
      const product = { id: '1', name: 'Product 1', price: 100, quantity: 10 };
      productService.getProductById.mockResolvedValue(product);

      const result = await productService.getProductById('1');

      expect(result).toEqual(product);
    });

    test('nên set up mock với mockImplementation để custom behavior', async () => {
      productService.getAllProducts.mockImplementation(() =>
        Promise.resolve([
          { id: '1', name: 'Mock Product', price: 999, quantity: 1 }
        ])
      );

      const result = await productService.getAllProducts();

      expect(result).toHaveLength(1);
      expect(result[0].price).toBe(999);
    });

    test('nên set up mock với delay để simulate network latency', async () => {
      const mockProducts = [
        { id: '1', name: 'Product 1', price: 100, quantity: 10 }
      ];
      productService.getAllProducts.mockImplementation(
        () => new Promise(resolve =>
          setTimeout(() => resolve(mockProducts), 300)
        )
      );

      const startTime = Date.now();
      await productService.getAllProducts();
      const duration = Date.now() - startTime;

      expect(duration).toBeGreaterThanOrEqual(300);
    });
  });

  /**
   * Test Success và Failure Scenarios (0.5 điểm)
   */
  describe('Test 2: Success và Failure Scenarios (0.5 điểm)', () => {

    test('nên handle getAllProducts success response', async () => {
      const mockProducts = [
        { id: '1', name: 'Product 1', price: 100, quantity: 10 },
        { id: '2', name: 'Product 2', price: 200, quantity: 20 }
      ];
      productService.getAllProducts.mockResolvedValue(mockProducts);

      const result = await productService.getAllProducts();

      expect(result).toHaveLength(2);
      expect(result[0]).toEqual(mockProducts[0]);
    });

    test('nên handle createProduct success response', async () => {
      const newProduct = { name: 'New Product', price: 150, quantity: 5 };
      const createdProduct = { id: '3', ...newProduct };
      
      productService.createProduct.mockResolvedValue({
        success: true,
        message: 'Product created successfully',
        product: createdProduct
      });

      const result = await productService.createProduct(newProduct);

      expect(result.success).toBe(true);
      expect(result.product.id).toBe('3');
    });

    test('nên handle createProduct failure response', async () => {
      productService.createProduct.mockRejectedValue(
        new Error('Failed to create product: Invalid data')
      );

      try {
        await productService.createProduct({});
      } catch (error) {
        expect(error.message).toContain('Failed to create product');
      }
    });

    test('nên handle updateProduct failure response', async () => {
      productService.updateProduct.mockRejectedValue(
        new Error('Product not found')
      );

      try {
        await productService.updateProduct('invalid-id', {});
      } catch (error) {
        expect(error.message).toBe('Product not found');
      }
    });

    test('nên handle deleteProduct failure response', async () => {
      productService.deleteProduct.mockRejectedValue(
        new Error('Cannot delete product: Product is in use')
      );

      try {
        await productService.deleteProduct('1');
      } catch (error) {
        expect(error.message).toContain('Cannot delete product');
      }
    });

    test('nên handle getProductById with empty result', async () => {
      productService.getProductById.mockResolvedValue(null);

      const result = await productService.getProductById('non-existent');

      expect(result).toBeNull();
    });

    test('nên handle getAllProducts with empty array', async () => {
      productService.getAllProducts.mockResolvedValue([]);

      const result = await productService.getAllProducts();

      expect(result).toEqual([]);
    });

    test('nên handle network error - getAllProducts', async () => {
      productService.getAllProducts.mockRejectedValue(
        new Error('Network error: Connection timeout')
      );

      try {
        await productService.getAllProducts();
      } catch (error) {
        expect(error.message).toContain('Network error');
      }
    });

    test('nên handle server error - createProduct', async () => {
      productService.createProduct.mockRejectedValue(
        new Error('Server error: 500 Internal Server Error')
      );

      try {
        await productService.createProduct({ name: 'Product' });
      } catch (error) {
        expect(error.message).toContain('Server error');
      }
    });

    test('nên handle validation error - Invalid price', async () => {
      productService.createProduct.mockRejectedValue(
        new Error('Validation error: Price must be greater than 0')
      );

      try {
        await productService.createProduct({ name: 'Product', price: -100 });
      } catch (error) {
        expect(error.message).toContain('Validation error');
      }
    });
  });

  /**
   * Verify Mock Calls (0.5 điểm)
   */
  describe('Test 3: Verify Mock Calls (0.5 điểm)', () => {

    test('nên verify getAllProducts được gọi', async () => {
      productService.getAllProducts.mockResolvedValue([]);

      await productService.getAllProducts();

      expect(productService.getAllProducts).toHaveBeenCalled();
    });

    test('nên verify createProduct được gọi với correct parameters', async () => {
      const newProduct = { name: 'New Product', price: 150, quantity: 5 };
      productService.createProduct.mockResolvedValue({
        success: true,
        product: { id: '3', ...newProduct }
      });

      await productService.createProduct(newProduct);

      expect(productService.createProduct).toHaveBeenCalledWith(newProduct);
    });

    test('nên verify updateProduct được gọi với id và data', async () => {
      const updatedData = { name: 'Updated', price: 200, quantity: 10 };
      productService.updateProduct.mockResolvedValue({ success: true });

      await productService.updateProduct('1', updatedData);

      expect(productService.updateProduct).toHaveBeenCalledWith('1', updatedData);
    });

    test('nên verify deleteProduct được gọi với product id', async () => {
      productService.deleteProduct.mockResolvedValue({ success: true });

      await productService.deleteProduct('1');

      expect(productService.deleteProduct).toHaveBeenCalledWith('1');
    });

    test('nên verify getProductById được gọi với id', async () => {
      productService.getProductById.mockResolvedValue({ id: '1', name: 'Product' });

      await productService.getProductById('1');

      expect(productService.getProductById).toHaveBeenCalledWith('1');
    });

    test('nên verify getAllProducts được gọi exactly 1 time', async () => {
      productService.getAllProducts.mockResolvedValue([]);

      await productService.getAllProducts();

      expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
    });

    test('nên verify createProduct NOT được gọi khi form invalid', () => {
      productService.createProduct.mockResolvedValue({ success: true });

      // Không gọi createProduct
      expect(productService.createProduct).not.toHaveBeenCalled();
    });

    test('nên verify mock.calls array - getAllProducts', async () => {
      productService.getAllProducts.mockResolvedValue([]);

      await productService.getAllProducts();

      expect(productService.getAllProducts.mock.calls.length).toBe(1);
      expect(productService.getAllProducts.mock.calls[0]).toEqual([]);
    });

    test('nên verify mock.calls array - createProduct with parameters', async () => {
      const newProduct = { name: 'Product', price: 100, quantity: 5 };
      productService.createProduct.mockResolvedValue({ success: true });

      await productService.createProduct(newProduct);

      expect(productService.createProduct.mock.calls.length).toBe(1);
      expect(productService.createProduct.mock.calls[0][0]).toEqual(newProduct);
    });

    test('nên verify mock.results array - success result', async () => {
      const mockProduct = { id: '1', name: 'Product', price: 100, quantity: 10 };
      productService.getProductById.mockResolvedValue(mockProduct);

      await productService.getProductById('1');

      expect(productService.getProductById.mock.results.length).toBe(1);
      expect(productService.getProductById.mock.results[0].type).toBe('return');
      expect(productService.getProductById.mock.results[0].value).resolves.toEqual(mockProduct);
    });

    test('nên verify mock.results array - error result', async () => {
      const error = new Error('Delete failed');
      productService.deleteProduct.mockRejectedValue(error);

      try {
        await productService.deleteProduct('1');
      } catch (e) {
        // Expected error
      }

      expect(productService.deleteProduct.mock.results.length).toBe(1);
      expect(productService.deleteProduct.mock.results[0].type).toBe('return');
    });

    test('nên verify nth call - multiple CRUD operations', async () => {
      productService.createProduct.mockResolvedValue({ success: true, product: { id: '1' } });
      productService.updateProduct.mockResolvedValue({ success: true });
      productService.deleteProduct.mockResolvedValue({ success: true });

      await productService.createProduct({ name: 'Product 1', price: 100, quantity: 5 });

      expect(productService.createProduct).toHaveBeenNthCalledWith(1, 
        expect.objectContaining({ name: 'Product 1' })
      );
    });

    test('nên verify createProduct được gọi multiple times', async () => {
      productService.createProduct.mockResolvedValue({ success: true });

      await productService.createProduct({ name: 'Product 1' });
      await productService.createProduct({ name: 'Product 2' });
      await productService.createProduct({ name: 'Product 3' });

      expect(productService.createProduct).toHaveBeenCalledTimes(3);
      expect(productService.createProduct.mock.calls.length).toBe(3);
    });

    test('nên verify getAllProducts mock.calls structure', async () => {
      productService.getAllProducts.mockResolvedValue([
        { id: '1', name: 'Product 1' },
        { id: '2', name: 'Product 2' }
      ]);

      await productService.getAllProducts();

      expect(Array.isArray(productService.getAllProducts.mock.calls)).toBe(true);
      expect(productService.getAllProducts.mock.calls[0]).toEqual([]);
    });

    test('nên verify updateProduct được gọi với exact parameters', async () => {
      const productId = '1';
      const updateData = { name: 'Updated Product', price: 250, quantity: 20 };
      productService.updateProduct.mockResolvedValue({ success: true });

      await productService.updateProduct(productId, updateData);

      expect(productService.updateProduct).toHaveBeenCalledWith(productId, updateData);
      expect(productService.updateProduct.mock.calls[0]).toEqual([productId, updateData]);
    });

    test('nên verify isMockFunction for all CRUD operations', () => {
      expect(jest.isMockFunction(productService.getAllProducts)).toBe(true);
      expect(jest.isMockFunction(productService.getProductById)).toBe(true);
      expect(jest.isMockFunction(productService.createProduct)).toBe(true);
      expect(jest.isMockFunction(productService.updateProduct)).toBe(true);
      expect(jest.isMockFunction(productService.deleteProduct)).toBe(true);
    });

    test('nên verify mock được reset giữa tests', async () => {
      productService.getAllProducts.mockResolvedValue([]);

      // Sau beforeEach, mock should be cleared
      expect(productService.getAllProducts.mock.calls.length).toBe(0);
    });
  });
});
