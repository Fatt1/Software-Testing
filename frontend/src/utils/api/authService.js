// Mock user database
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

export const login = async (email, password) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500));

  if (!email || !password) {
    throw new Error('Email và mật khẩu không được để trống');
  }

  if (!email.includes('@')) {
    throw new Error('Email không hợp lệ');
  }

  const user = mockUsers.find(
    u => u.email === email && u.password === password
  );

  if (user) {
    // Simulate JWT token
    const token = `mock-jwt-token-${user.id}`;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({
      id: user.id,
      email: user.email,
      name: user.name,
      role: user.role
    }));

    return {
      success: true,
      data: {
        token,
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
};

export const logout = async () => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 200));
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  return JSON.parse(userStr);
};

export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};