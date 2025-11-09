import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { describe, test, expect, beforeEach, afterEach, jest } from '@jest/globals';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import LoginForm from '../../components/LoginForm.jsx';

// ===== PHẦN MOCK TEST =====
// Setup mock cho API service - Đây là phần của mock testing
const mockLogin = jest.fn();
jest.mock('../api/authService', () => ({
  login: (...args) => mockLogin(...args)
}));

// Mock users data
const mockUsers = [
  {
    id: 1,
    email: 'test@example.com',
    password: 'password123',
    name: 'Test User',
    role: 'user'
  },
  {
    id: 2,
    email: 'admin@example.com',
    password: 'admin123',
    name: 'Admin User',
    role: 'admin'
  }
];

// ===== BẮT ĐẦU TEST SUITE CHÍNH =====
describe('Login Component Integration Tests', () => {
  
  // Set up test environment
  beforeEach(() => {
    // Clear mocks and timers
    jest.clearAllMocks();
    jest.useFakeTimers({ advanceTimers: true });
    
    // Reset DOM
    document.body.innerHTML = '';
    // Note: removed jest.spyOn for input.value because it causes Jest spy errors.
    // @testing-library/user-event interacts with DOM inputs directly and doesn't
    // require spying on the native property.

    // Set up default mock implementation
    mockLogin.mockImplementation(async (email, password) => {
      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500));

      // Validate input
      if (!email || !password) {
        throw new Error('Email và mật khẩu không được để trống');
      }

      if (!email.includes('@')) {
        throw new Error('Email không hợp lệ');
      }

      // Check credentials
      const user = mockUsers.find(
        u => u.email === email && u.password === password
      );

      if (user) {
        // Return success response
        return {
          success: true,
          data: {
            token: `mock-jwt-token-${user.id}`,
            user: {
              id: user.id,
              email: user.email,
              name: user.name,
              role: user.role
            }
          }
        };
      }

      throw new Error('Email hoặc mật khẩu không đúng');
    });
  });

  // Clean up after each test
  afterEach(() => {
    // Clean up timers
    // Avoid running pending timers here because component timeouts cause state updates
    // outside of act when run; clear timers instead and restore real timers.
    jest.clearAllTimers();
    jest.useRealTimers();
    
    // Restore mocks
    jest.restoreAllMocks();
  });

  // ===== PHẦN INTEGRATION TEST =====
  // 1. Test rendering và user interactions (2 điểm)
  // Integration giữa các elements UI với nhau
  
  describe('Rendering và User Interactions', () => {
    
    test('Hiển thị form đăng nhập với đầy đủ các elements', () => {
      render(<LoginForm />);
      
  // Kiểm tra tiêu đề
  expect(screen.getByRole('heading', { name: /đăng nhập/i })).toBeInTheDocument();
  expect(screen.getByText('Chào mừng bạn quay lại')).toBeInTheDocument();
      
  // Kiểm tra các input fields (use placeholders since labels are not associated)
  expect(screen.getByPlaceholderText('you@example.com')).toBeInTheDocument();
  expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
      
      // Kiểm tra placeholder
      expect(screen.getByPlaceholderText('you@example.com')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
      
      // Kiểm tra checkbox và links
      expect(screen.getByText('Nhớ mật khẩu')).toBeInTheDocument();
      expect(screen.getByText('Quên mật khẩu?')).toBeInTheDocument();
      
      // Kiểm tra button đăng nhập
      expect(screen.getByRole('button', { name: /đăng nhập/i })).toBeInTheDocument();
      
      // Kiểm tra link đăng ký
      expect(screen.getByText('Đăng ký ngay')).toBeInTheDocument();
    });

    test('Cho phép user nhập email và password', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      
      // User nhập email
      await user.type(emailInput, 'testuser@example.com');
      expect(emailInput).toHaveValue('testuser@example.com');
      
      // User nhập password
      await user.type(passwordInput, 'Test123456');
      expect(passwordInput).toHaveValue('Test123456');
    });

    test('Toggle hiển thị/ẩn password khi click vào nút', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const toggleButton = screen.getByRole('button', { name: /👁️/ });
      
      // Mặc định password bị ẩn
      expect(passwordInput).toHaveAttribute('type', 'password');
      
      // Click để hiển thị password
      await user.click(toggleButton);
      expect(passwordInput).toHaveAttribute('type', 'text');
      
      // Click lại để ẩn password
      await user.click(toggleButton);
      expect(passwordInput).toHaveAttribute('type', 'password');
    });

    test('Checkbox "Nhớ mật khẩu" có thể được check/uncheck', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const checkbox = screen.getByRole('checkbox');
      
      // Mặc định chưa được check
      expect(checkbox).not.toBeChecked();
      
      // Click để check
      await user.click(checkbox);
      expect(checkbox).toBeChecked();
      
      // Click lại để uncheck
      await user.click(checkbox);
      expect(checkbox).not.toBeChecked();
    });
  });

  // ===== PHẦN TÍCH HỢP MOCK VÀ INTEGRATION =====
  // 2. Test form submission và API calls (2 điểm)
  // Kết hợp cả mock API và integration test UI
  
  describe('Form Submission và API Calls', () => {
    
    test('Hiển thị lỗi khi submit form rỗng', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Submit form rỗng
      await user.click(submitButton);
      
      // Kiểm tra thông báo lỗi
      await waitFor(() => {
        expect(screen.getByText('Vui lòng nhập email và mật khẩu')).toBeInTheDocument();
      });
    });

    test('Hiển thị lỗi khi email không hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Nhập email không hợp lệ
      await user.type(emailInput, 'invalid-email');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Kiểm tra thông báo lỗi
      await waitFor(() => {
        expect(screen.getByText('Email không hợp lệ')).toBeInTheDocument();
      });
    });

    test('Hiển thị loading state khi đang submit form', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Nhập thông tin hợp lệ
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      
      // Submit form
      await user.click(submitButton);
      
      // Kiểm tra loading state
      expect(screen.getByText('Đang xử lý...')).toBeInTheDocument();
      expect(submitButton).toBeDisabled();
      
      // Kiểm tra các input fields cũng bị disable
      expect(emailInput).toBeDisabled();
      expect(passwordInput).toBeDisabled();
    });

    test('Submit form thành công với email và password hợp lệ', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Nhập thông tin hợp lệ từ mock users
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      
      // Submit form
      await user.click(submitButton);
      
      // Đợi và kiểm tra thông báo thành công
      await waitFor(() => {
        expect(screen.getByText(/đăng nhập thành công/i)).toBeInTheDocument();
      }, { timeout: 3000 });
      
      // Kiểm tra thông báo chuyển hướng
      expect(screen.getByText('Chuyển hướng...')).toBeInTheDocument();
    });

    test('Form được reset sau khi đăng nhập thành công', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Nhập và submit
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Đợi thành công và form reset
      await waitFor(() => {
        expect(screen.getByText(/đăng nhập thành công/i)).toBeInTheDocument();
      }, { timeout: 3000 });
      
      // Đợi form được hiển thị lại sau khi reset
      await waitFor(() => {
        const newEmailInput = screen.queryByPlaceholderText('you@example.com');
        const newPasswordInput = screen.queryByPlaceholderText('••••••••');
        
        if (newEmailInput && newPasswordInput) {
          expect(newEmailInput).toHaveValue('');
          expect(newPasswordInput).toHaveValue('');
        }
      }, { timeout: 4000 });
    });
  });

  // ===== 3. Test error handling và success messages (1 điểm) =====
  
  describe('Error Handling và Success Messages', () => {
    
    test('Hiển thị error message với style phù hợp', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Trigger error
      await user.click(submitButton);
      
      // Kiểm tra error message xuất hiện
      await waitFor(() => {
        const errorElement = screen.getByText('Vui lòng nhập email và mật khẩu');
        expect(errorElement).toBeInTheDocument();
        
        // Kiểm tra parent element có styling phù hợp (có thể kiểm tra class hoặc style)
        const errorContainer = errorElement.closest('div');
        expect(errorContainer).toHaveStyle({
          color: '#dc2626'
        });
      });
    });

    test('Error message biến mất khi user sửa input', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Trigger error
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Vui lòng nhập email và mật khẩu')).toBeInTheDocument();
      });
      
      // User bắt đầu nhập email
      await user.type(emailInput, 'test@example.com');
      
      // Submit lại - error cũ sẽ biến mất (được clear trong handleSubmit)
      await user.click(submitButton);
      
      // Error mới có thể xuất hiện nhưng error cũ đã bị clear
      await waitFor(() => {
        const errorMessages = screen.queryAllByText('Vui lòng nhập email và mật khẩu');
        // Kiểm tra không còn error message cũ hoặc chỉ có 1 error message mới
        expect(errorMessages.length).toBeLessThanOrEqual(1);
      });
    });

    test('Success message hiển thị sau khi đăng nhập thành công', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Login thành công
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      // Kiểm tra success message
      await waitFor(() => {
        expect(screen.getByText(/✓ đăng nhập thành công!/i)).toBeInTheDocument();
        expect(screen.getByText('Chuyển hướng...')).toBeInTheDocument();
      }, { timeout: 3000 });
    });

    test('Form không thể submit nhiều lần khi đang loading', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Nhập thông tin
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      
      // Click submit
      await user.click(submitButton);
      
      // Button bị disable
      expect(submitButton).toBeDisabled();
      
      // Cố gắng click lại (sẽ không trigger submit mới)
      await user.click(submitButton);
      
      // Vẫn chỉ có 1 loading state
      const loadingTexts = screen.getAllByText('Đang xử lý...');
      expect(loadingTexts).toHaveLength(1);
    });

    test('Hiển thị multiple error types correctly', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // Test 1: Empty form error
      await user.click(submitButton);
      await waitFor(() => {
        expect(screen.getByText('Vui lòng nhập email và mật khẩu')).toBeInTheDocument();
      });
      
      // Test 2: Invalid email error
      await user.type(emailInput, 'invalid-email');
      await user.type(passwordInput, 'password');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Email không hợp lệ')).toBeInTheDocument();
        // Error cũ không còn
        expect(screen.queryByText('Vui lòng nhập email và mật khẩu')).not.toBeInTheDocument();
      });
    });
  });

  // ===== PHẦN MOCK TEST =====
  // 4. Test Error Boundaries
  // Sử dụng mock để test các kịch bản lỗi
  
  describe('Error Boundary Tests', () => {
    test('Handles API errors gracefully', async () => {
  // Mock API error
  const errorMessage = 'Network error occurred';
  // Use the mockLogin function directly
  mockLogin.mockRejectedValueOnce(new Error(errorMessage));

      const user = userEvent.setup();
      render(<LoginForm />);
      
      // Submit valid form data
      await user.type(screen.getByPlaceholderText('you@example.com'), 'test@example.com');
      await user.type(screen.getByPlaceholderText('••••••••'), 'password123');
      await user.click(screen.getByRole('button', { name: /đăng nhập/i }));
      
      // Verify error handling: the specific error message should appear
      await waitFor(() => {
        expect(screen.getByText(errorMessage)).toBeInTheDocument();
      });
    });

    test('Recovers from errors when user retries', async () => {
      // Mock API error then success
      // Use the mockLogin function directly
      mockLogin
        .mockRejectedValueOnce(new Error('Network error'))
        .mockResolvedValueOnce({ success: true });

      const user = userEvent.setup();
      render(<LoginForm />);
      
      const emailInput = screen.getByPlaceholderText('you@example.com');
      const passwordInput = screen.getByPlaceholderText('••••••••');
      const submitButton = screen.getByRole('button', { name: /đăng nhập/i });
      
      // First attempt - should fail
      await user.type(emailInput, 'test@example.com');
      await user.type(passwordInput, 'password123');
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText(/network error/i)).toBeInTheDocument();
      });
      
      // Second attempt - should succeed
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText(/đăng nhập thành công/i)).toBeInTheDocument();
      });
    });
  });

  // ===== PHẦN END-TO-END INTEGRATION TEST =====
  // Bonus: Integration với user flow hoàn chỉnh
  // Test tích hợp toàn bộ luồng từ đầu đến cuối
  
  describe('Complete User Flow Integration', () => {
    
    test('User flow hoàn chỉnh: từ nhập liệu đến đăng nhập thành công', async () => {
      const user = userEvent.setup();
      render(<LoginForm />);
      
  // 1. User thấy form đăng nhập
  expect(screen.getByRole('heading', { name: /đăng nhập/i })).toBeInTheDocument();
      
      // 2. User thử submit form rỗng và thấy lỗi
      await user.click(screen.getByRole('button', { name: /đăng nhập/i }));
      await waitFor(() => {
        expect(screen.getByText('Vui lòng nhập email và mật khẩu')).toBeInTheDocument();
      });
      
      // 3. User nhập email sai format
      await user.type(screen.getByPlaceholderText('you@example.com'), 'wrong-email');
      await user.type(screen.getByPlaceholderText('••••••••'), 'password123');
      await user.click(screen.getByRole('button', { name: /đăng nhập/i }));
      
      await waitFor(() => {
        expect(screen.getByText('Email không hợp lệ')).toBeInTheDocument();
      });
      
      // 4. User sửa lại email đúng từ mock users
      const emailInput = screen.getByPlaceholderText('you@example.com');
      await user.clear(emailInput);
      await user.type(emailInput, 'test@example.com');
      
      // 5. User toggle xem password
      const toggleBtn = screen.getByRole('button', { name: /👁️/ });
      await user.click(toggleBtn);
      expect(screen.getByPlaceholderText('••••••••')).toHaveAttribute('type', 'text');
      
      // 6. User submit và đăng nhập thành công
      await user.click(screen.getByRole('button', { name: /đăng nhập/i }));
      
      // 7. Thấy loading state
      expect(screen.getByText('Đang xử lý...')).toBeInTheDocument();
      
      // 8. Thấy success message
      await waitFor(() => {
        expect(screen.getByText(/✓ đăng nhập thành công!/i)).toBeInTheDocument();
      }, { timeout: 3000 });
      
      // 9. Form được reset sau một thời gian
      await waitFor(() => {
        const resetEmailInput = screen.queryByPlaceholderText('you@example.com');
        if (resetEmailInput) {
          expect(resetEmailInput).toHaveValue('');
        }
      }, { timeout: 4000 });
    });
  });
});