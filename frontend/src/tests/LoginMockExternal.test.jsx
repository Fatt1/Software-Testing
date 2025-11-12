import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '../components/LoginForm';
import * as authService from '../services/authService';
import '@testing-library/jest-dom';

jest.mock('../services/authService');

/**
 * Mock External Dependencies - Login Component
 * Test mocking authService và verify mock calls
 */
describe('Login - Mock External Dependencies', () => {
  
  beforeEach(() => {
    jest.clearAllMocks();
  });

  /**
   * Mock authService.login()
   */
  describe('Test 1: Mock authService.login() (1 điểm)', () => {

    test('nên mock authService.login() function', () => {
      // Verify authService.login được mock
      expect(typeof authService.login).toBe('function');
      expect(jest.isMockFunction(authService.login)).toBe(true);
    });

    test('nên set up mock với mockResolvedValue', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true, user: { email: 'test@example.com' } });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify mock được gọi
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });

    test('nên set up mock với mockRejectedValue', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Login failed'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify mock được gọi
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });

    test('nên set up mock với mockImplementation', async () => {
      const user = userEvent.setup();
      authService.login.mockImplementation((email, password) => {
        if (email && password) {
          return Promise.resolve({ success: true });
        }
        return Promise.reject(new Error('Invalid credentials'));
      });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });

    test('nên set up mock để throw error sau delay', async () => {
      const user = userEvent.setup();
      authService.login.mockImplementation(() => 
        new Promise((_, reject) => 
          setTimeout(() => reject(new Error('Network timeout')), 200)
        )
      );
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });

    test('nên set up mock để return data sau delay', async () => {
      const user = userEvent.setup();
      authService.login.mockImplementation(() => 
        new Promise(resolve => 
          setTimeout(() => resolve({ success: true }), 300)
        )
      );
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });
  });

  /**
   * Test với mocked successful/failed responses (1 điểm)
   */
  describe('Test 2: Mocked Successful/Failed Responses (1 điểm)', () => {

    test('nên handle mocked successful response', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ 
        success: true, 
        message: 'Login successful',
        user: { 
          id: '123', 
          email: 'test@example.com',
          name: 'Test User'
        }
      });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify success message appears
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 2000 });
    });

    test('nên handle mocked failed response - Invalid credentials', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Invalid credentials'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'wrongpassword');
      await user.click(submitButton);
      
      // Verify error message appears
      await waitFor(() => {
        const errorMessage = screen.getByText(/Invalid credentials/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên handle mocked failed response - Network error', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Network error'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify error message appears
      await waitFor(() => {
        const errorMessage = screen.getByText(/Network error/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên handle mocked failed response - Server error', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Server error'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify error message appears
      await waitFor(() => {
        const errorMessage = screen.getByText(/Server error/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên handle mocked response với user data', async () => {
      const user = userEvent.setup();
      const mockUserData = {
        success: true,
        user: {
          id: 'user-123',
          email: 'john@example.com',
          name: 'John Doe',
          role: 'admin'
        }
      };
      authService.login.mockResolvedValue(mockUserData);
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'john@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify success message
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalledWith('john@example.com', 'password123');
      });
    });

    test('nên handle mocked response với empty user data', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ 
        success: true, 
        user: null 
      });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Mock được gọi
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });
  });

  /**
   * Verify mock calls
   */
  describe('Test 3: Verify Mock Calls (0.5 điểm)', () => {

    test('nên verify mock được gọi với correct parameters', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'user@example.com');
      await user.type(passwordInput, 'pass123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify mock được gọi với đúng parameters
        expect(authService.login).toHaveBeenCalledWith('user@example.com', 'pass123');
      });
    });

    test('nên verify mock được gọi exactly 1 time', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify called exactly once
        expect(authService.login).toHaveBeenCalledTimes(1);
      });
    });

    test('nên verify mock NOT được gọi khi invalid data', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      // Submit form trống
      await user.click(submitButton);
      
      // Mock không nên được gọi
      expect(authService.login).not.toHaveBeenCalled();
    });

    test('nên verify mock được gọi với specific call', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'first@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify first call
        expect(authService.login).toHaveBeenNthCalledWith(1, 'first@example.com', 'password123');
      });
    });

    test('nên verify mock.mock.calls array', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify mock.calls array
        expect(authService.login.mock.calls.length).toBe(1);
        expect(authService.login.mock.calls[0][0]).toBe('test@example.com');
        expect(authService.login.mock.calls[0][1]).toBe('password123');
      });
    });

    test('nên verify mock.results array', async () => {
      const user = userEvent.setup();
      const mockResponse = { success: true, user: { id: '123' } };
      authService.login.mockResolvedValue(mockResponse);
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify mock results
        expect(authService.login.mock.results.length).toBe(1);
        expect(authService.login.mock.results[0].type).toBe('return');
      });
    });
  });
});
