export default {
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
  },
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.jsx?$': 'babel-jest',
  },
  testMatch: ['**/__tests__/**/*.js',    '**/__tests__/**/*.[jt]s?(x)',
    '**/?(*.)+(spec|test).[jt]s?(x)', '**/*.test.js', '**/*.spec.js'],
  collectCoverageFrom: [
    'src/**/*.js',
    '!src/main.jsx',
    '!src/index.js',
  ],
};