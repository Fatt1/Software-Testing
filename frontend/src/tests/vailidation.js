/**
 * Kiểm tra hợp lệ tên đăng nhập
 * Yêu cầu:
 * - Không được để trống
 * - Độ dài từ 3-50 ký tự
 * - Chỉ cho phép ký tự a-z,  A-Z, 0-9, -, ., _
 *
 * @param {string} username - Tên đăng nhập cần kiểm tra
 * @returns {object} - { isValid: boolean, error: string|null }
 */
export const validateUsername = (username) => {
  // Check if empty
  if (!username || username.trim().length === 0) {
    return {
      isValid: false,
      error: 'Username không được để trống'
    };
  }
  // Check length
  if (username.length < 3) {
    return {
      isValid: false,
      error: 'Username phải ít nhất 3 ký tự'
    };
  }
  if (username.length > 50) {
    return {
      isValid: false,
      error: 'Username không được vượt quá 50 ký tự'
    };
  }
  // Check invalid characters
  const validPattern = /^[a-zA-Z._-]+$/;
  if (!validPattern.test(username)) {
    return {
      isValid: false,
      error: 'Username chỉ chứa chữ, chấm, gạch dưới, và gạch ngang'
    };
  }
  return {
    isValid: true,
    error: null
  };
};

/**
 * Kiểm tra hợp lệ mật khẩu
 * Yêu cầu:
 * - Không được để trống
 * - Độ dài từ 6-100 ký tự
 * - Phải chứa ít nhất một chữ cái và một số
 *
 * @param {string} password - Mật khẩu cần kiểm tra
 * @returns {object} - { isValid: boolean, error: string|null }
 */
export const validatePassword = (password) => {
  // Check if empty
  if (!password || password.length === 0) {
    return {
      isValid: false,
      error: 'Mật khẩu không được để trống'
    };
  }
  // Check length
  if (password.length < 6) {
    return {
      isValid: false,
      error: 'Mật khẩu phải ít nhất 6 ký tự'
    };
  }
  if (password.length > 100) {
    return {
      isValid: false,
      error: 'Mật khẩu không được vượt quá 100 ký tự'
    };
  }
  // Check for at least one letter
  const hasLetter = /[a-zA-Z]/.test(password);
  if (!hasLetter) {
    return {
      isValid: false,
      error: 'Mật khẩu phải chứa ít nhất một chữ cái'
    };
  }
  // Check for at least one number
  const hasNumber = /[0-9]/.test(password);
  if (!hasNumber) {
    return {
      isValid: false,
      error: 'Mật khẩu phải chứa ít nhất một số'
    };
  }
  return {
    isValid: true,
    error: null
  };
};