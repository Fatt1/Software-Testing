module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/jest.setup.cjs'], // Đảm bảo file này có tồn tại, nếu không thì xóa dòng này đi
  
  moduleNameMapper: {
      '\\.(css|less|scss|sass)$': '<rootDir>/src/styleMock.js',
    },

  transform: {
    '^.+\\.(js|jsx)$': ['babel-jest', { 
      configFile: './babel.config.cjs',
      babelrc: false,
      rootMode: 'upward',
    }],
  },
  transformIgnorePatterns: [
    'node_modules/(?!(@testing-library|identity-obj-proxy)/)',
  ],
  testMatch: [
    '**/src/**/*.test.js',
    '**/src/**/*.test.jsx',
  ],
  collectCoverageFrom: [
    'src/**/*.{js,jsx}',
    '!src/main.jsx',
    '!src/**/*.test.{js,jsx}',
    '!src/index.css',
  ],
};