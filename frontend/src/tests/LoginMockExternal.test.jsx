/**
 * @see ../components/LoginForm.jsx - Component under test
 * @see ../services/authService.js - Mocked external service
 */

import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '../components/LoginForm';
import * as authService from '../services/authService';
import '@testing-library/jest-dom';

// Mock the entire authService module
// All exported functions will be replaced with Jest mock functions
jest.mock('../services/authService');

/**
 * Mock External Dependencies - Login Component
 * Test mocking authService và verify mock calls
 */
describe('Login - Mock External Dependencies', () => {
  
  beforeEach(() => {
    jest.clearAllMocks();
  });


  describe('Test 1: Mock authService.login() (1 điểm)', () => {

    test('nên mock authService.login() function', () => {
      // Verify authService.login được mock
      expect(typeof authService.login).toBe('function');
      expect(jest.isMockFunction(authService.login)).toBe(true);
    });
  });

  /**
   * Test với mocked successful/failed responses 
   */
  describe('Test 2: Mocked Successful/Failed Responses (1 điểm)', () => {

    test('nên handle mocked successful response', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ 
        success: true, 
        message: 'Login successful',
        user: { 
          id: '123', 
          username: 'testuser',
          name: 'Test User'
        }
      });
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
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
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
      await user.type(passwordInput, 'wrongpassword1');
      await user.click(submitButton);
      
      // Verify error message appears
      await waitFor(() => {
        const errorMessage = screen.getByText(/Invalid credentials/i);
        expect(errorMessage).toBeInTheDocument();
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
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'user123');
      await user.type(passwordInput, 'pass123');
      await user.click(submitButton);
      
      await waitFor(() => {
        // Verify mock được gọi với đúng parameters
        expect(authService.login).toHaveBeenCalledWith('user123', 'pass123');
      });
    });

    test('nên verify mock được gọi exactly 1 time', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
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
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      // Submit form trống
      await user.click(submitButton);
      
      // Mock không nên được gọi
      expect(authService.login).not.toHaveBeenCalled();
    });

  });
});
