/**
 * Validate username input for login/registration
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
 */
export const validateUsername = (username) => {
  // Check if empty or whitespace only
  if (!username || username.trim().length === 0) {
    return {
      isValid: false,
      error: "Username không được để trống",
    };
  }
  // Check minimum length (3 characters)
  if (username.length < 3) {
    return {
      isValid: false,
      error: "Username phải ít nhất 3 ký tự",
    };
  }
  // Check maximum length (50 characters)
  if (username.length > 50) {
    return {
      isValid: false,
      error: "Username không được vượt quá 50 ký tự",
    };
  }
  // Check for valid character pattern (letters, numbers, dot, underscore, hyphen only)
  const validPattern = /^[a-zA-Z0-9._-]+$/;
  if (!validPattern.test(username)) {
    return {
      isValid: false,
      error: "Username chỉ chứa chữ, số, dấu chấm (.), gạch dưới (_), và gạch ngang (-)",
    };
  }
  // All validations passed
  return {
    isValid: true,
    error: null,
  };
};

/**
 * Validate password input for login/registration
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
 */
export const validatePassword = (password) => {
  // Check if empty
  if (!password || password.length === 0) {
    return {
      isValid: false,
      error: "Mật khẩu không được để trống",
    };
  }
  // Check minimum length (6 characters for basic security)
  if (password.length < 6) {
    return {
      isValid: false,
      error: "Mật khẩu phải ít nhất 6 ký tự",
    };
  }
  // Check maximum length (prevent extremely long passwords)
  if (password.length > 100) {
    return {
      isValid: false,
      error: "Mật khẩu không được vượt quá 100 ký tự",
    };
  }
  // Check for at least one letter (a-z or A-Z)
  const hasLetter = /[a-zA-Z]/.test(password);
  if (!hasLetter) {
    return {
      isValid: false,
      error: "Mật khẩu phải chứa ít nhất một chữ cái",
    };
  }
  // Check for at least one number (0-9)
  const hasNumber = /[0-9]/.test(password);
  if (!hasNumber) {
    return {
      isValid: false,
      error: "Mật khẩu phải chứa ít nhất một số",
    };
  }
  // All validations passed - password is strong enough
  return {
    isValid: true,
    error: null,
  };
};

/**
 * Calculate password strength based on various criteria
 * Returns strength level and score for UI display
 *
 * @function getPasswordStrength
 * @param {string} password - Password to evaluate
 * @returns {Object} Strength evaluation object
 * @returns {number} return.score - Strength score (0-4)
 * @returns {string} return.label - Strength label (Rất yếu, Yếu, Trung bình, Mạnh, Rất mạnh)
 * @returns {string} return.color - Color for UI display
 */
export const getPasswordStrength = (password) => {
  if (!password) {
    return { score: 0, label: '', color: '#e5e7eb' };
  }

  let score = 0;
  
  // Length scoring
  if (password.length >= 6) score++;
  if (password.length >= 8) score++;
  if (password.length >= 12) score++;
  
  // Character variety scoring
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++; // Has both cases
  if (/[0-9]/.test(password)) score++; // Has numbers
  if (/[^a-zA-Z0-9]/.test(password)) score++; // Has special chars
  
  // Reduce score if too simple
  if (/^[a-zA-Z]+$/.test(password) || /^[0-9]+$/.test(password)) score = Math.max(0, score - 2);
  
  // Normalize score to 0-4 range
  score = Math.min(4, Math.floor(score * 4 / 6));
  
  const strengthMap = {
    0: { label: 'Rất yếu', color: '#ef4444' },
    1: { label: 'Yếu', color: '#f97316' },
    2: { label: 'Trung bình', color: '#eab308' },
    3: { label: 'Mạnh', color: '#22c55e' },
    4: { label: 'Rất mạnh', color: '#10b981' }
  };
  
  return { score, ...strengthMap[score] };
};
