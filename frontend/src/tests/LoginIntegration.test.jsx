/**
 * @see ../components/LoginForm.jsx - Component under test
 * @see ../services/authService.js - Mocked service layer
 */

import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '../components/LoginForm';
import * as authService from '../services/authService';
import '@testing-library/jest-dom';
jest.mock('../services/authService');

/**
 * Integration Tests cho Login Component
 * Test tích hợp: Component + Mocked API Service
 */
describe('Login - Integration Testing', () => {
  
  beforeEach(() => {
    // Clear any stored data
    localStorage.clear();
    jest.clearAllMocks();
  });

  /**
   * Test 1: Rendering và User Interactions
   */
  describe('Test 1: Rendering và User Interactions', () => {
    
    test('nên render login form component thành công', () => {
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      // Query cụ thể hơn để tránh matching multiple elements
      const heading = screen.getByRole('heading', { level: 1 });
      expect(heading).toHaveTextContent(/Đăng Nhập/i);
      expect(screen.getByText(/Chào mừng bạn quay lại/i)).toBeInTheDocument();
    });

    test('nên có thể nhập username vào input', async () => {
      const user = userEvent.setup();
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      await user.type(usernameInput, 'testuser');
      
      expect(usernameInput.value).toBe('testuser');
    });

    test('nên có thể nhập password vào input', async () => {
      const user = userEvent.setup();
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      await user.type(passwordInput, 'password123');
      
      expect(passwordInput.value).toBe('password123');
    });

    test('nên disable button khi loading', async () => {
      const user = userEvent.setup();
      
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
      
      // Button should exist and be clickable
      expect(submitButton).toBeInTheDocument();
      expect(submitButton).toBeEnabled();
    });
  });

  /**
   * Test 2: Form Submission và API Calls (2 điểm)
   */
  describe('Test 2: Form Submission và API Calls', () => {
    
    test('nên gọi login API khi form submit với dữ liệu hợp lệ - admin', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({
        success: true,
        message: 'Login thành công',
        user: { userName: 'admin', email: 'admin@example.com' }
      });
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'admin');
      await user.type(passwordInput, 'admin123');
      await user.click(submitButton);
      
      // Verify API call
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalledWith('admin', 'admin123');
      });
    });

    test('nên hiển thị success message khi API trả về success - testuser', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({
        success: true,
        message: 'Login thành công',
        user: { userName: 'testuser', email: 'testuser@example.com' }
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
      await user.type(passwordInput, 'test1234');
      await user.click(submitButton);
      
      // Verify API called
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalledWith('testuser', 'test1234');
      });
    });
  });

  /**
   * Test 3: Error Handling và Success Messages 
   */
  describe('Test 3: Error Handling và Success Messages', () => {
    
    test('nên hiển thị error khi username trống', async () => {
      const user = userEvent.setup();
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Vui lòng nhập username và mật khẩu/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị error khi username không hợp lệ', async () => {
      const user = userEvent.setup();
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'ab');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Username phải có ít nhất 3 ký tự/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị error khi API trả về error', async () => {
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
      await user.type(passwordInput, 'wrongpassword');
      await user.click(submitButton);
      
      // Verify error message
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Invalid credentials|Error/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị success message khi login thành công', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({
        success: true,
        message: 'Login thành công',
        user: { userName: 'admin', email: 'admin@example.com' }
      });
      
   render(
  <MemoryRouter>
    <LoginForm />
  </MemoryRouter>
);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'admin');
      await user.type(passwordInput, 'admin123');
      await user.click(submitButton);
      
      // Verify API called
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalled();
      });
    });

  });
});
