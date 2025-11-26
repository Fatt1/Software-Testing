/**
 * Validate username input for login/registration
 * 
 * Performs comprehensive validation of username according to business rules.
 * Checks for required field, length constraints, and allowed character set.
 * 
 * Validation Rules:
 * - Cannot be empty or whitespace only
 * - Length must be between 3-50 characters
 * - Only allows: letters (a-z, A-Z), dot (.), underscore (_), hyphen (-)
 * - No spaces or special characters
 * 
 * @function validateUsername
 * @param {string} username - Username string to validate
 * @returns {Object} Validation result object
 * @returns {boolean} return.isValid - True if validation passes
 * @returns {string|null} return.error - Error message if validation fails, null otherwise
 * 
 * @example
 * // Valid username
 * validateUsername('john_doe')
 * // Returns: { isValid: true, error: null }
 * 
 * @example
 * // Invalid username (too short)
 * validateUsername('ab')
 * // Returns: { isValid: false, error: 'Username phải ít nhất 3 ký tự' }
 * 
 * @example
 * // Invalid username (special characters)
 * validateUsername('user@123')
 * // Returns: { isValid: false, error: 'Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang' }
 */
export const validateUsername = (username) => {
  // Check if empty or whitespace only
  if (!username || username.trim().length === 0) {
    return {
      isValid: false,
      error: 'Username không được để trống'
    };
  }
  // Check minimum length (3 characters)
  if (username.length < 3) {
    return {
      isValid: false,
      error: 'Username phải ít nhất 3 ký tự'
    };
  }
  // Check maximum length (50 characters)
  if (username.length > 50) {
    return {
      isValid: false,
      error: 'Username không được vượt quá 50 ký tự'
    };
  }
  // Check for valid character pattern (letters, dot, underscore, hyphen only)
  const validPattern = /^[a-zA-Z._-]+$/;
  if (!validPattern.test(username)) {
    return {
      isValid: false,
      error: 'Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang'
    };
  }
  // All validations passed
  return {
    isValid: true,
    error: null
  };
};

/**
 * Validate password input for login/registration
 * 
 * Performs comprehensive password validation according to security requirements.
 * Ensures password meets minimum strength criteria.
 * 
 * Validation Rules:
 * - Cannot be empty
 * - Length must be between 6-100 characters
 * - Must contain at least one letter (a-z or A-Z)
 * - Must contain at least one number (0-9)
 * - Combination of letters and numbers increases security
 * 
 * @function validatePassword
 * @param {string} password - Password string to validate
 * @returns {Object} Validation result object
 * @returns {boolean} return.isValid - True if validation passes
 * @returns {string|null} return.error - Error message if validation fails, null otherwise
 * 
 * @example
 * // Valid password
 * validatePassword('admin123')
 * // Returns: { isValid: true, error: null }
 * 
 * @example
 * // Invalid password (too short)
 * validatePassword('abc')
 * // Returns: { isValid: false, error: 'Mật khẩu phải ít nhất 6 ký tự' }
 * 
 * @example
 * // Invalid password (no numbers)
 * validatePassword('abcdef')
 * // Returns: { isValid: false, error: 'Mật khẩu phải chứa ít nhất một số' }
 * 
 * @example
 * // Invalid password (no letters)
 * validatePassword('123456')
 * // Returns: { isValid: false, error: 'Mật khẩu phải chứa ít nhất một chữ cái' }
 */
export const validatePassword = (password) => {
  // Check if empty
  if (!password || password.length === 0) {
    return {
      isValid: false,
      error: 'Mật khẩu không được để trống'
    };
  }
  // Check minimum length (6 characters for basic security)
  if (password.length < 6) {
    return {
      isValid: false,
      error: 'Mật khẩu phải ít nhất 6 ký tự'
    };
  }
  // Check maximum length (prevent extremely long passwords)
  if (password.length > 100) {
    return {
      isValid: false,
      error: 'Mật khẩu không được vượt quá 100 ký tự'
    };
  }
  // Check for at least one letter (a-z or A-Z)
  const hasLetter = /[a-zA-Z]/.test(password);
  if (!hasLetter) {
    return {
      isValid: false,
      error: 'Mật khẩu phải chứa ít nhất một chữ cái'
    };
  }
  // Check for at least one number (0-9)
  const hasNumber = /[0-9]/.test(password);
  if (!hasNumber) {
    return {
      isValid: false,
      error: 'Mật khẩu phải chứa ít nhất một số'
    };
  }
  // All validations passed - password is strong enough
  return {
    isValid: true,
    error: null
  };
};