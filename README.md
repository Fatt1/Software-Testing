# Software-Testing

## 📋 Project Overview

This is a comprehensive **Software Testing** project demonstrating modern testing practices for a full-stack web application. The project implements a **Product Management System** with authentication, showcasing various testing levels and methodologies.

## 🎯 Project Purpose

This project serves as:
- **Educational Resource**: Demonstrates professional testing practices in software development
- **Testing Portfolio**: Showcases unit, integration, and end-to-end testing implementations
- **Best Practices Reference**: Follows industry standards for React/Spring Boot testing
- **Team Collaboration**: Structured for group software testing coursework

## 🏗️ Architecture

### Technology Stack

**Backend:**
- **Framework**: Spring Boot 3.5.7 with Java 21
- **Security**: JWT authentication, BCrypt password hashing
- **Database**: SQL Server (production) + H2 (in-memory testing)
- **ORM**: JPA/Hibernate for data persistence
- **Testing**: JUnit 5, Mockito, Spring Boot Test

**Frontend:**
- **Framework**: React 19.1.1 with Vite 7.1.7 build tool
- **HTTP Client**: Axios for API communication
- **UI**: Custom CSS components with responsive design
- **Testing**: Jest 29.7.0, Cypress 15.6.0, React Testing Library 16.3.0

### Project Structure

```
Software-Testing/
├── backend/              # Spring Boot REST API
│   ├── src/main/java/   # Application source code
│   │   ├── controller/  # REST API endpoints
│   │   ├── service/     # Business logic layer
│   │   ├── entity/      # JPA entity models
│   │   └── repository/  # Data access layer
│   └── src/test/java/   # Backend test suite
│
├── frontend/            # React SPA application
│   ├── src/
│   │   ├── components/  # React UI components
│   │   ├── services/    # API client services
│   │   ├── utils/       # Validation utilities
│   │   └── tests/       # Frontend test suite
│   │       └── cypress/ # E2E test scenarios
│   ├── test-reports/    # Test execution reports
│   └── cypress/         # Cypress configuration
│
└── performance-tests/   # Load testing scripts
    ├── scripts/         # K6 performance tests
    └── results/         # Performance test results
```

## 🧪 Testing Strategy

### Testing Pyramid Implementation

**1. Unit Tests** (Base layer - fastest, most numerous)
- **Backend**: Service layer logic, validation functions
- **Frontend**: React components in isolation, utility functions
- **Tools**: JUnit, Jest, Mockito
- **Coverage**: Individual functions and methods

**2. Integration Tests** (Middle layer - moderate speed)
- **Backend**: Controller + Service + Repository integration
- **Frontend**: Component + Service layer integration
- **Tools**: Spring Boot Test, React Testing Library
- **Coverage**: Multiple layers working together

**3. End-to-End Tests** (Top layer - slower, realistic)
- **Full Stack**: Complete user workflows from UI to database
- **Tools**: Cypress for browser automation
- **Coverage**: Critical user journeys (login, CRUD operations)

**4. Performance Tests** (Non-functional testing)
- **Load Testing**: Stress testing with concurrent users
- **Tools**: K6 performance testing framework
- **Scenarios**: 100, 500, 1000 concurrent users

## 🔍 Test Coverage Areas

### Backend Testing
- ✅ Authentication API endpoints (AuthController)
- ✅ Product CRUD operations (ProductController)
- ✅ Business logic validation (AuthService, ProductService)
- ✅ JWT token generation and validation
- ✅ Database integration with H2

### Frontend Testing
- ✅ Login form component (UI + validation)
- ✅ Product management component (CRUD operations)
- ✅ API service layer (authService, productService)
- ✅ Validation utilities (input validation, product validation)
- ✅ Cypress E2E scenarios (login flows, product management)

### Testing Patterns Used
- **Page Object Model**: Cypress tests use POM pattern for maintainability
- **Mocking**: External dependencies mocked for isolated testing
- **Fixtures**: Test data stored in reusable fixtures
- **Setup/Teardown**: BeforeEach hooks for clean test state

## 🚀 Getting Started

### Prerequisites
- **Java**: JDK 21 or higher
- **Node.js**: v18 or higher
- **Maven**: 3.8+ (for backend builds)
- **Git**: For version control

### Running the Application

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
# API runs on http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# App runs on http://localhost:5173
```

### Running Tests

**Backend Tests:**
```bash
cd backend
./mvnw test                    # Run all tests
./mvnw test -Dtest=ClassName   # Run specific test class
```

**Frontend Tests:**
```bash
cd frontend
npm test                       # Run Jest unit/integration tests
npm run cypress:open          # Open Cypress test runner
npm run cypress:run           # Run Cypress tests headless
```

**Performance Tests:**
```bash
cd performance-tests
k6 run scripts/login-test.js   # Run login load test
k6 run scripts/product-test.js # Run product CRUD load test
```

## 📊 Test Reports

Test execution generates detailed reports:
- **Jest**: JSON reports in `frontend/test-reports/`
- **Cypress**: Screenshots/videos in `frontend/cypress/screenshots/`
- **Coverage**: Code coverage reports for quality metrics

## 👥 Team Collaboration

### GitHub Workflow
- **Repository**: Fatt1/Software-Testing
- **Branch Strategy**: Main branch for stable code
- **Commit Convention**: `docs:`, `test:`, `feat:`, `fix:` prefixes
- **Contributions**: All team members tracked via Git commits

### Team Guidelines
- Write tests before implementing features (TDD approach)
- Maintain code coverage above 70%
- Document all test scenarios clearly
- Review test failures before merging

## 📚 Documentation

- **E2E Testing Guide**: `frontend/E2E_TEST_GUIDE.md`
- **Test Setup Instructions**: `frontend/TEST_SETUP.md`
- **Performance Testing**: `performance-tests/START_HERE.md`
- **Automation Tests**: `AUTOMATION_TESTS_README.md`

## 🎓 Learning Outcomes

By studying this project, you will learn:
- ✅ How to structure a testable full-stack application
- ✅ Writing effective unit, integration, and E2E tests
- ✅ Mocking strategies for isolated testing
- ✅ Test automation with CI/CD pipelines
- ✅ Performance testing and load analysis
- ✅ Professional testing documentation practices

## 📝 License

This is an educational project for Software Testing coursework.

## 🤝 Contributors

Team members and their contributions are tracked through Git commit history.
