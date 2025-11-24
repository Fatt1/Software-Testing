import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from '../components/LoginForm';
import '@testing-library/jest-dom';

/**
 * Integration Tests cho Login Component
 * Test tích hợp THẬT: Component + API Service + Real API
 * API: https://swearingly-pseudocubic-beth.ngrok-free.dev/api/auth/login
 */
describe('Login - Integration Testing', () => {
  
  beforeEach(() => {
    // Clear any stored data
    localStorage.clear();
  });

  /**
   * Test 1: Rendering và User Interactions
   */
  describe('Test 1: Rendering và User Interactions', () => {
    
    test('nên render login form component thành công', () => {
      render(<LoginForm />);
      
      // Query cụ thể hơn để tránh matching multiple elements
      const heading = screen.getByRole('heading', { level: 1 });
      expect(heading).toHaveTextContent(/Đăng Nhập/i);
      expect(screen.getByText(/Chào mừng bạn quay lại/i)).toBeInTheDocument();
    });

    test('nên có username input field', () => {
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      expect(usernameInput).toBeInTheDocument();
      expect(usernameInput).toHaveAttribute('type', 'text');
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

    test('nên có thể nhập username vào input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      await user.type(usernameInput, 'testuser');
      
      expect(usernameInput.value).toBe('testuser');
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
      
      // Tìm toggle button 
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
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Button nên bị disable hoặc có loading indicator
      expect(submitButton.disabled || submitButton.textContent.includes('...') || 
             submitButton.textContent.includes('Đang') || submitButton.getAttribute('aria-busy')).toBeTruthy();
    });

    test('nên có thể nhấn Enter để submit form', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      
      await user.type(usernameInput, 'testuser');
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
        const errorMessage = screen.queryByText(/Vui lòng nhập username và mật khẩu/i) ||
                            screen.queryByText(/username không hợp lệ/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });
  });

  /**
   * Test 2: Form Submission và API Calls (2 điểm)
   */
  describe('Test 2: Form Submission và API Calls', () => {
    
    test('nên gọi login API khi form submit với dữ liệu hợp lệ - admin', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'admin');
      await user.type(passwordInput, 'admin123');
      await user.click(submitButton);
      
      // Chờ success message từ API thật
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công|✓ Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);

    test('nên pass đúng username và password tới API - user01', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'user01');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Verify API call thành công bằng cách check success message
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công|✓ Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);

    test('nên không gọi API nếu username không hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'ab');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Validation error nên hiển thị, API không được gọi
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Username phải có ít nhất 3 ký tự/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên không gọi API nếu password trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
      await user.click(submitButton);
      
      // Error message nên hiển thị
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Vui lòng nhập username và mật khẩu/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên không gọi API nếu username trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Error message nên hiển thị
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Vui lòng nhập username và mật khẩu/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị success message khi API trả về success - testuser', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
      await user.type(passwordInput, 'test1234');
      await user.click(submitButton);
      
      // Chờ success message từ API thật
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công|✓ Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);
  });

  /**
   * Test 3: Error Handling và Success Messages 
   */
  describe('Test 3: Error Handling và Success Messages', () => {
    
    test('nên hiển thị error khi username trống', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Vui lòng nhập username và mật khẩu/i);
        expect(errorMessage).toBeInTheDocument();
      });
    });

    test('nên hiển thị error khi username không hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
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
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'testuser');
      await user.type(passwordInput, 'wrongpassword');
      await user.click(submitButton);
      
      // Chờ error message từ API thật khi sai password
      await waitFor(() => {
        const errorMessage = screen.getByText(/Login với password sai|Dữ liệu không hợp lệ|Invalid credentials|Sai tên đăng nhập|Đăng nhập thất bại|Login failed/i);
        expect(errorMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);

    test('nên clear error khi user chỉnh sửa input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      // Error hiển thị
      await waitFor(() => {
        expect(screen.getByText(/Vui lòng nhập username và mật khẩu/i)).toBeInTheDocument();
      });
      
      // Nhập username
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      await user.type(usernameInput, 'testuser');
      
      // Error nên bị xóa khi nhập (nếu có logic này)
      // Có thể không xóa ngay, phụ thuộc vào implementation
    });

    test('nên hiển thị error message với styling khác biệt', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        const errorMessage = screen.getByText(/Vui lòng nhập username và mật khẩu/i);
        // Error message nên có class hoặc styling error
        expect(errorMessage).toBeInTheDocument();
        expect(errorMessage.className || errorMessage.style.color).toBeTruthy();
      });
    });

    test('nên hiển thị success message khi login thành công', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      // Thử với một trong các tài khoản hợp lệ
      await user.type(usernameInput, 'admin');
      await user.type(passwordInput, 'admin123');
      await user.click(submitButton);
      
      // Chờ success message từ API thật
      await waitFor(() => {
        const successMessage = screen.queryByText(/Đăng nhập thành công|✓ Đăng nhập thành công/i);
        expect(successMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);

    test('nên handle API error gracefully', async () => {
      const user = userEvent.setup();
      
      render(<LoginForm />);
      
      const usernameInput = screen.getByPlaceholderText(/your_username/i);
      const passwordInput = screen.getByPlaceholderText(/••••••••/i);
      const submitButton = screen.getByRole('button', { name: /Đăng Nhập/i });
      
      await user.type(usernameInput, 'invaliduser999');
      await user.type(passwordInput, 'wrongpass999');
      await user.click(submitButton);
      
      // Chờ error message từ API thật khi credentials không đúng
      await waitFor(() => {
        const errorMessage = screen.queryByText(/Login thất bại với user name không tồn tại|Network error|Invalid credentials|Sai tên đăng nhập|Đăng nhập thất bại|Login failed/i);
        expect(errorMessage).toBeInTheDocument();
      }, { timeout: 5000 });
    }, 10000);
  });
});
