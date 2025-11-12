import React, { useState } from 'react';
import { login } from '../services/authService';
import './LoginForm.css';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    if (!email || !password) {
      setError('Vui lòng nhập email và mật khẩu');
      setIsLoading(false);
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('Email không hợp lệ');
      setIsLoading(false);
      return;
    }

    try {
      // call auth service (mocked in tests)
      const res = await login(email, password);
      if (res && res.success) {
        setSuccess(true);
        setIsLoading(false);

        setTimeout(() => {
          setSuccess(false);
          setEmail('');
          setPassword('');
        }, 2000);
        return;
      }
      // fallback
      setError('Có lỗi xảy ra');
      setIsLoading(false);
    } catch (err) {
      setError(err?.message || 'Có lỗi xảy ra');
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.loginContainer}>
        {/* Header */}
        <div style={styles.header}>
          <div style={styles.logoWrapper}>
            <div style={styles.logoCircle}>
              <span style={styles.logoIcon}>🔐</span>
            </div>
          </div>
          <h1 style={styles.title}>Đăng Nhập</h1>
          <p style={styles.subtitle}>Chào mừng bạn quay lại</p>
        </div>

        {/* Card */}
        <div style={styles.card}>
          {success ? (
            <div style={styles.successMessage}>
              <p style={styles.successTitle}>✓ Đăng nhập thành công!</p>
              <p style={styles.successSubtitle}>Chuyển hướng...</p>
            </div>
          ) : (
            <div>
              {/* Email Input */}
              <div style={styles.formGroup}>
                <label style={styles.label}>Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  style={styles.input}
                  disabled={isLoading}
                />
              </div>

              {/* Password Input */}
              <div style={styles.formGroup}>
                <label style={styles.label}>Mật Khẩu</label>
                <div style={styles.passwordWrapper}>
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    style={styles.input}
                    disabled={isLoading}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    style={styles.toggleBtn}
                    disabled={isLoading}
                  >
                    {showPassword ? '👁️‍🗨️' : '👁️'}
                  </button>
                </div>
              </div>

              {/* Remember & Forgot Password */}
              <div style={styles.optionsRow}>
                <label style={styles.checkboxLabel}>
                  <input
                    type="checkbox"
                    style={styles.checkbox}
                    disabled={isLoading}
                  />
                  <span>Nhớ mật khẩu</span>
                </label>
                <a href="#" style={styles.forgotLink}>Quên mật khẩu?</a>
              </div>

              {/* Error Message */}
              {error && (
                <div style={styles.errorMessage}>
                  {error}
                </div>
              )}

              {/* Submit Button */}
              <button
                onClick={handleSubmit}
                disabled={isLoading}
                style={{
                  ...styles.submitBtn,
                  opacity: isLoading ? 0.7 : 1,
                  cursor: isLoading ? 'not-allowed' : 'pointer'
                }}
              >
                {isLoading ? (
                  <>
                    <span style={styles.spinner}>⚙</span>
                    <span>Đang xử lý...</span>
                  </>
                ) : (
                  <>
                    <span>→</span>
                    <span>Đăng Nhập</span>
                  </>
                )}
              </button>

              {/* Divider */}
              <div style={styles.divider}></div>

              {/* Sign Up Link */}
              <p style={styles.signupText}>
                Chưa có tài khoản?{' '}
                <a href="#" style={styles.signupLink}>Đăng ký ngay</a>
              </p>
            </div>
          )}
        </div>

        {/* Footer */}
        <p style={styles.footer}>
          © 2025 Your Company. All rights reserved.
        </p>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '16px',
    fontFamily: 'Arial, sans-serif'
  },

  loginContainer: {
    width: '100%',
    maxWidth: '400px',
    animation: 'slideInUp 0.6s ease-out'
  },

  header: {
    textAlign: 'center',
    marginBottom: '32px'
  },

  logoWrapper: {
    display: 'inline-block',
    marginBottom: '16px',
    animation: 'float 3s ease-in-out infinite'
  },

  logoCircle: {
    width: '64px',
    height: '64px',
    background: 'white',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
    margin: '0 auto'
  },

  logoIcon: {
    fontSize: '32px'
  },

  title: {
    fontSize: '36px',
    fontWeight: 'bold',
    color: 'white',
    margin: '8px 0'
  },

  subtitle: {
    fontSize: '14px',
    color: 'rgba(255, 255, 255, 0.7)',
    margin: 0
  },

  card: {
    background: 'white',
    borderRadius: '16px',
    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
    padding: '32px',
    animation: 'slideInUp 0.6s ease-out'
  },

  formGroup: {
    marginBottom: '24px',
    animation: 'slideInUp 0.6s ease-out 0.1s backwards'
  },

  label: {
    display: 'block',
    fontSize: '14px',
    fontWeight: '600',
    color: '#374151',
    marginBottom: '8px'
  },

  input: {
    width: '100%',
    padding: '12px 16px',
    borderRadius: '8px',
    border: '2px solid rgba(255, 255, 255, 0.3)',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    fontSize: '14px',
    transition: 'all 0.3s ease',
    boxSizing: 'border-box',
    outline: 'none',
    cursor: 'pointer'
  },

  passwordWrapper: {
    position: 'relative',
    display: 'flex',
    alignItems: 'center'
  },

  toggleBtn: {
    position: 'absolute',
    right: '12px',
    background: 'none',
    border: 'none',
    fontSize: '18px',
    cursor: 'pointer',
    padding: '4px 8px',
    transition: 'transform 0.2s ease',
    color: '#6b7280'
  },

  optionsRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px',
    fontSize: '14px'
  },

  checkboxLabel: {
    display: 'flex',
    alignItems: 'center',
    color: '#6b7280',
    cursor: 'pointer'
  },

  checkbox: {
    marginRight: '8px',
    cursor: 'pointer'
  },

  forgotLink: {
    color: '#3b82f6',
    textDecoration: 'none',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'color 0.2s ease'
  },

  errorMessage: {
    background: 'rgba(239, 68, 68, 0.1)',
    borderLeft: '4px solid #ef4444',
    color: '#dc2626',
    padding: '12px',
    borderRadius: '6px',
    fontSize: '14px',
    marginBottom: '24px',
    animation: 'shake 0.5s ease-in-out'
  },

  successMessage: {
    background: 'rgba(34, 197, 94, 0.1)',
    borderLeft: '4px solid #22c55e',
    color: '#16a34a',
    padding: '16px',
    borderRadius: '6px',
    textAlign: 'center',
    animation: 'slideInUp 0.4s ease-out'
  },

  successTitle: {
    fontWeight: '600',
    margin: '0 0 4px 0',
    fontSize: '16px'
  },

  successSubtitle: {
    fontSize: '14px',
    margin: '0',
    opacity: 0.8
  },

  submitBtn: {
    width: '100%',
    padding: '12px 16px',
    borderRadius: '8px',
    border: 'none',
    background: 'linear-gradient(135deg, #3b82f6, #2563eb)',
    color: 'white',
    fontWeight: '600',
    fontSize: '18px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    cursor: 'pointer',
    transition: 'all 0.3s ease',
    animation: 'slideInUp 0.6s ease-out 0.3s backwards'
  },

  spinner: {
    display: 'inline-block',
    animation: 'spin 1s linear infinite'
  },

  divider: {
    height: '1px',
    background: 'linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.1), transparent)',
    margin: '24px 0',
    border: 'none'
  },

  signupText: {
    textAlign: 'center',
    color: '#6b7280',
    fontSize: '14px',
    margin: 0
  },

  signupLink: {
    color: '#3b82f6',
    textDecoration: 'none',
    fontWeight: '600',
    cursor: 'pointer'
  },

  footer: {
    textAlign: 'center',
    color: 'rgba(255, 255, 255, 0.6)',
    fontSize: '12px',
    marginTop: '24px'
  }
};