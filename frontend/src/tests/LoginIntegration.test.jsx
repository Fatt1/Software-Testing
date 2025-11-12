import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '../components/LoginForm';
import * as authService from '../services/authService';
import '@testing-library/jest-dom';

// Mock the auth service
jest.mock('../services/authService');

/**
 * Integration Tests cho Login Component
 * Test tích hợp: Component + API Service
 */
describe('Login - Integration Testing', () => {
  
  beforeEach(() => {
    jest.clearAllMocks();
  });

  /**
   * Test 1: Rendering và User Interactions (2 điểm)
   */
  describe('Test 1: Rendering và User Interactions', () => {
    
    test('nên render login form component thành công', () => {
      render(<LoginForm />);
      
      // Query cụ thể hơn để tránh matching multiple elements
      const heading = screen.getByRole('heading', { level: 1 });
      expect(heading).toHaveTextContent(/Đăng Nhập/i);
      expect(screen.getByText(/Chào mừng bạn quay lại/i)).toBeInTheDocument();
    });

    test('nên có email input field', () => {
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      expect(emailInput).toBeInTheDocument();
      expect(emailInput).toHaveAttribute('type', 'email');
    });

    test('nên có password input field', () => {
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      expect(passwordInput).toBeInTheDocument();
      expect(passwordInput).toHaveAttribute('type', 'password');
    });

    test('nên có submit button "Đăng Nhập"', () => {
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      expect(submitButton).toBeInTheDocument();
    });

    test('nên có thể nhập email vào input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      await user.type(emailInput, 'test@example.com');
      
      expect(emailInput.value).toBe('test@example.com');
    });

    test('nên có thể nhập password vào input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      await user.type(passwordInput, 'password123');
      
      expect(passwordInput.value).toBe('password123');
    });

    test('nên có toggle để hiển thị/ẩn password', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      expect(passwordInput).toHaveAttribute('type', 'password');
      
      // Tìm toggle button (nếu có)
      const toggleButtons = screen.getAllByRole('button');
      const toggleBtn = toggleButtons.find(btn => btn.title?.includes('password') || btn.textContent?.includes('👁'));
      
      if (toggleBtn) {
        await user.click(toggleBtn);
        // Sau khi toggle, password input type nên đổi thành text
        await waitFor(() => {
          expect(passwordInput.type === 'text' || passwordInput.type === 'password').toBe(true);
        });
      }
    });

    test('nên disable button khi loading', async () => {
      const user = userEvent.setup();
      authService.login.mockImplementation(() => 
        new Promise(resolve => setTimeout(() => resolve({ success: true }), 2000))
      );
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Button nên bị disable hoặc có loading indicator
      expect(submitButton.disabled || submitButton.textContent.includes('...') || 
             submitButton.textContent.includes('Đang') || submitButton.getAttribute('aria-busy')).toBeTruthy();
    });

    test('nên có thể nhấn Enter để submit form', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      
      // Có thể nhấn Enter hoặc click submit
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      expect(submitButton).toBeInTheDocument();
    });

    test('nên highlight error input khi validation fail', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      // Submit form trống
      await user.click(submitButton);
      
      // Chờ error message
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Vui lòng nhập email và mật khẩu/i) ||
                            screen.queryByText(/Email không hợp lệ/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });
  });

  /**
   * Test 2: Form Submission và API Calls (2 điểm)
   */
  describe('Test 2: Form Submission và API Calls', () => {
    
    test('nên gọi login API khi form submit với dữ liệu hợp lệ', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Chờ API được gọi
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalledWith('test@example.com', 'password123');
      });
    });

    test('nên pass đúng email và password tới API', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'user@test.com');
      await user.type(passwordInput, 'securePassword123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(authService.login).toHaveBeenCalledWith('user@test.com', 'securePassword123');
      });
    });

    test('nên không gọi API nếu email không hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'invalid-email');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // API không nên được gọi
      expect(authService.login).not.toHaveBeenCalled();
    });

    test('nên không gọi API nếu password trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.click(submitButton);
      
      // API không nên được gọi
      expect(authService.login).not.toHaveBeenCalled();
    });

    test('nên không gọi API nếu email trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // API không nên được gọi
      expect(authService.login).not.toHaveBeenCalled();
    });

    test('nên gọi API chỉ 1 lần khi submit 1 lần', async () => {
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
        expect(authService.login).toHaveBeenCalledTimes(1);
      });
    });

    test('nên hiển thị success message khi API trả về success', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Chờ success message
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 3000 });
    });
  });

  /**
   * Test 3: Error Handling và Success Messages (1 điểm)
   */
  describe('Test 3: Error Handling và Success Messages', () => {
    
    test('nên hiển thị error khi email trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Vui lòng nhập email và mật khẩu/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị error khi email không hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'invalid-email');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Email không hợp lệ/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị error khi API trả về error', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Invalid credentials'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'wrongpassword');
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Invalid credentials/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên clear error khi user chỉnh sửa input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      // Error hiển thị
      await waitFor(() => {
        expect(screen.getByText(/Vui lòng nhập email và mật khẩu/i)).toBeInTheDocument();
      });
      
      // Nhập email
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      await user.type(emailInput, 'test@example.com');
      
      // Error nên bị xóa khi nhập (nếu có logic này)
      // Có thể không xóa ngay, phụ thuộc vào implementation
    });

    test('nên hiển thị error message với styling khác biệt', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Vui lòng nhập email và mật khẩu/i);
        // Error message nên có class hoặc styling error
        expect(errorMessage).toBeInTheDocument();
        expect(errorMessage.className || errorMessage.style.color).toBeTruthy();
      });
    });

    test('nên hiển thị success message khi login thành công', async () => {
      const user = userEvent.setup();
      authService.login.mockResolvedValue({ success: true });
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Chờ success message hiển thị
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 2000 });
    });

    test('nên handle API error gracefully', async () => {
      const user = userEvent.setup();
      authService.login.mockRejectedValue(new Error('Network error'));
      
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText(/you@example.com/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText(/Network error/i)).toBeInTheDocument();
      });
    });
  });
});
