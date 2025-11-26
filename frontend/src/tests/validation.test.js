/**
 * Validation Utility Unit Tests - Username & Password Validation
 * 
 * Test Suite Purpose:
 * Pure unit tests for validation utility functions that enforce business rules
 * for username and password inputs. These are critical security and UX functions.
 * 
 * Functions Under Test:
 * 1. validateUsername(username) - Validates username format and constraints
 * 2. validatePassword(password) - Validates password strength and requirements
 * 
 * Testing Strategy:
 * - Pure Unit Testing: Test functions in complete isolation
 * - Boundary Testing: Test edge cases and limits
 * - Negative Testing: Test invalid inputs
 * - Positive Testing: Test valid inputs
 * - Return Value Validation: Check { isValid, error } structure
 * 
 * Username Validation Rules:
 * - Not empty/null/whitespace only
 * - Minimum length: 3 characters
 * - Maximum length: 50 characters
 * - Allowed characters: letters, numbers, underscores
 * - No special characters or spaces
 * 
 * Password Validation Rules:
 * - Not empty/null/whitespace only
 * - Minimum length: 6 characters
 * - Maximum length: 50 characters
 * - Must contain at least one letter
 * - Must contain at least one number
 * - Optionally: special characters allowed
 * 
 * Test Categories:
 * 
 * validateUsername() Tests:
 * 1. Empty/Null Cases (1 điểm) - Empty string, null, whitespace
 * 2. Length Validation (1 điểm) - Too short (<3), too long (>50)
 * 3. Character Validation - Valid alphanumeric and underscores
 * 4. Invalid Characters - Special chars, spaces, symbols
 * 5. Boundary Cases - Exactly 3 chars, exactly 50 chars
 * 
 * validatePassword() Tests:
 * 1. Empty/Null Cases - Empty string, null, whitespace
 * 2. Length Validation - Too short (<6), too long (>50)
 * 3. Complexity Rules - Must have letters and numbers
 * 4. Valid Passwords - Meeting all requirements
 * 5. Edge Cases - Boundary lengths, special characters
 * 
 * Expected Return Format:
 * Success: { isValid: true }
 * Failure: { isValid: false, error: 'Error message in Vietnamese' }
 * 
 * Why These Tests Matter:
 * - Security: Prevent weak passwords
 * - UX: Provide clear error messages
 * - Data Quality: Ensure valid usernames
 * - Business Rules: Enforce requirements
 * - Regression Prevention: Catch validation breaks
 * 
 * @see ../utils/vailidation.js - Validation functions under test
 * @see ../components/LoginForm.jsx - Uses these validators
 */

import { validateUsername, validatePassword } from '../utils/vailidation.js';
import { describe, test, expect } from '@jest/globals';

/**
 * Unit Tests cho validateUsername()
 */
describe('validateUsername()', () => {
  
  /**
   * Test 1: Username rỗng (1 điểm)
   * - Input: '' hoặc null hoặc chỉ có khoảng trắng
   * - Kỳ vọng: isValid = false, có thông báo lỗi
   */
  describe('Test 1: Username rỗng', () => {
    test('nên trả về không hợp lệ khi username là chuỗi rỗng', () => {
      const result = validateUsername('');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username không được để trống');
    });

    test('nên trả về không hợp lệ khi username là null', () => {
      const result = validateUsername(null);
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username không được để trống');
    });

    test('nên trả về không hợp lệ khi username chỉ có khoảng trắng', () => {
      const result = validateUsername('   ');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username không được để trống');
    });
  });

  /**
   * Test 2: Username quá ngắn/quá dài (1 điểm)
   * - Quá ngắn: < 3 ký tự
   * - Quá dài: > 50 ký tự
   * - Hợp lệ: từ 3-20 ký tự
   */
  describe('Test 2: Username quá ngắn/quá dài', () => {
    test('nên trả về không hợp lệ khi username quá ngắn (ít hơn 3 ký tự)', () => {
      const result = validateUsername('ab');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username phải ít nhất 3 ký tự');
    });

    test('nên trả về không hợp lệ khi username quá dài (nhiều hơn 50 ký tự)', () => {
      const result = validateUsername('abcdefghijklmnopqrstuabcdefghijklmnopqrstuabcdefghijklmnopqrstu');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username không được vượt quá 50 ký tự');
    });

    test('nên hợp lệ với đúng 3 ký tự', () => {
      const result = validateUsername('abc');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với đúng 50 ký tự', () => {
      const result = validateUsername('abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });
  });

  /**
   * Test 3: Ký tự đặc biệt không hợp lệ (1 điểm)
   * - Không hợp lệ: khoảng trắng, @, #, $, ., v.v.
   * - Hợp lệ: chữ cái, số, gạch dưới (_), gạch ngang (-), chấm (.)
   */
  describe('Test 3: Ký tự đặc biệt không hợp lệ', () => {
    test('nên trả về không hợp lệ khi username chứa khoảng trắng', () => {
      const result = validateUsername('user name');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang');
    });

    test('nên trả về không hợp lệ khi username chứa ký tự @ ', () => {
      const result = validateUsername('user@name');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang');
    });

    test('nên trả về không hợp lệ khi username chứa ký tự #', () => {
      const result = validateUsername('user#name');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang');
    });

    test('nên trả về không hợp lệ khi username chứa ký tự $', () => {
      const result = validateUsername('user$name');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang');
    });

    test('nên trả về hợp lệ khi username chứa dấu chấm', () => {
      const result = validateUsername('user.name');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với gạch dưới (_)', () => {
      const result = validateUsername('user_name');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với gạch ngang (-)', () => {
      const result = validateUsername('user-name');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });
  });

  /**
   * Test 4: Username hợp lệ (1 điểm)
   * - Kiểm tra các định dạng username hợp lệ khác nhau
   */
  describe('Test 4: Username hợp lệ', () => {
    test('nên hợp lệ khi chỉ có chữ cái thường', () => {
      const result = validateUsername('username');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi có chữ hoa và chữ thường', () => {
      const result = validateUsername('UserName');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi có chữ cái và -', () => {
      const result = validateUsername('userbach-');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi có chữ cái, gạch dưới, chấm và gạch ngang', () => {
      const result = validateUsername('user_n.ame-');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

  });
});

/**
 * Unit Tests cho validatePassword()
 * Tổng cộng: 4 điểm
 */
describe('validatePassword()', () => {
  
  /**
   * Test 1: Password rỗng (1 điểm)
   * - Input: '' hoặc null hoặc undefined
   * - Kỳ vọng: isValid = false, có thông báo lỗi
   */
  describe('Test 1: Password rỗng', () => {
    test('nên trả về không hợp lệ khi password là chuỗi rỗng', () => {
      const result = validatePassword('');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu không được để trống');
    });

    test('nên trả về không hợp lệ khi password là null', () => {
      const result = validatePassword(null);
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu không được để trống');
    });

    test('nên trả về không hợp lệ khi password là undefined', () => {
      const result = validatePassword(undefined);
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu không được để trống');
    });
  });

  /**
   * Test 2: Password quá ngắn/quá dài (1 điểm)
   * - Quá ngắn: < 6 ký tự
   * - Quá dài: > 50 ký tự
   * - Hợp lệ: từ 6-50 ký tự
   */
  describe('Test 2: Password quá ngắn/quá dài', () => {
    test('nên trả về không hợp lệ khi password quá ngắn (ít hơn 6 ký tự)', () => {
      const result = validatePassword('pass1');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu phải ít nhất 6 ký tự');
    });

    test('nên trả về không hợp lệ khi password quá dài (nhiều hơn 100 ký tự)', () => {
      const longPassword = 'a'.repeat(95) + '12345b';
      const result = validatePassword(longPassword);
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu không được vượt quá 100 ký tự');
    });

    test('nên hợp lệ với đúng 6 ký tự', () => {
      const result = validatePassword('pass12');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với đúng 50 ký tự', () => {
      const password = 'a'.repeat(44) + '123456';
      const result = validatePassword(password);
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });
  });

  /**
   * Test 3: Password không có chữ hoặc số (1 điểm)
   * - Yêu cầu: phải có ít nhất một chữ cái
   * - Yêu cầu: phải có ít nhất một số
   */
  describe('Test 3: Password không có chữ hoặc số', () => {
    test('nên trả về không hợp lệ khi password không có chữ cái', () => {
      const result = validatePassword('123456');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu phải chứa ít nhất một chữ cái');
    });

    test('nên trả về không hợp lệ khi password không có số', () => {
      const result = validatePassword('password');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu phải chứa ít nhất một số');
    });

    test('nên trả về không hợp lệ khi password chỉ có ký tự đặc biệt', () => {
      const result = validatePassword('!@#$%^');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu phải chứa ít nhất một chữ cái');
    });

    test('nên trả về không hợp lệ khi password có chữ cái và ký tự đặc biệt nhưng không có số', () => {
      const result = validatePassword('pass!@#$');
      expect(result.isValid).toBe(false);
      expect(result.error).toBe('Mật khẩu phải chứa ít nhất một số');
    });
  });

  /**
   * Test 4: Password hợp lệ (1 điểm)
   * - Kiểm tra các định dạng password hợp lệ khác nhau
   */
  describe('Test 4: Password hợp lệ', () => {
    test('nên hợp lệ khi có chữ cái và số', () => {
      const result = validatePassword('password123');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi có chữ hoa, chữ thường và số', () => {
      const result = validatePassword('PassWord123');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi có chữ cái, số và ký tự đặc biệt', () => {
      const result = validatePassword('Pass@word123!');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với độ dài tối thiểu (6 ký tự: chữ cái và số)', () => {
      const result = validatePassword('abc123');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ với độ dài tối đa (50 ký tự)', () => {
      const password = 'a'.repeat(44) + '123456';
      const result = validatePassword(password);
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi chỉ có một chữ cái và các số khác', () => {
      const result = validatePassword('a123456');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });

    test('nên hợp lệ khi chỉ có một số và các chữ cái khác', () => {
      const result = validatePassword('password1');
      expect(result.isValid).toBe(true);
      expect(result.error).toBeNull();
    });
  });
});