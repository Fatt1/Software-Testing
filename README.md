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

---

## 🔌 API Documentation

### Authentication Endpoints

#### POST /api/auth/login
Authenticates user and returns JWT token.

**Request Body:**
```json
{
  "userName": "admin",
  "password": "admin123"
}
```

**Response (Success - 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "userDto": {
    "userName": "admin",
    "email": "admin@example.com"
  }
}
```

**Response (Error - 401 Unauthorized):**
```json
{
  "token": null,
  "message": "Invalid credentials",
  "userDto": null
}
```

**Validation Rules:**
- `userName`: Required, 3-50 characters, alphanumeric only
- `password`: Required, 6-100 characters, alphanumeric with special chars

**HTTP Status Codes:**
- `200 OK`: Login successful
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Validation errors
- `500 Internal Server Error`: Server error

---

### Product Endpoints

#### GET /api/products
Retrieve paginated list of all products.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Items per page (default: 10)

**Example Request:**
```
GET /api/products?page=0&size=20
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "productName": "Laptop Dell XPS 15",
      "price": 25000000,
      "quantity": 10,
      "category": "Điện tử",
      "description": "High-performance laptop for professionals"
    },
    {
      "id": 2,
      "productName": "iPhone 14 Pro",
      "price": 30000000,
      "quantity": 5,
      "category": "Điện tử",
      "description": "Latest iPhone with A16 Bionic chip"
    }
  ],
  "totalPages": 3,
  "totalElements": 42,
  "number": 0,
  "size": 20
}
```

#### GET /api/products/{id}
Retrieve a specific product by ID.

**Path Parameter:**
- `id`: Product ID (integer)

**Example Request:**
```
GET /api/products/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productName": "Laptop Dell XPS 15",
  "price": 25000000,
  "quantity": 10,
  "category": "Điện tử",
  "description": "High-performance laptop for professionals"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Product not found",
  "message": "Product with ID 999 does not exist"
}
```

#### POST /api/products
Create a new product.

**Request Headers:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "productName": "Samsung Galaxy S23",
  "price": 22000000,
  "quantity": 15,
  "category": "Điện tử",
  "description": "Flagship smartphone with advanced camera system and powerful processor"
}
```

**Validation Rules:**
- `productName`: Required, 3-100 characters, unique
- `price`: Required, positive number, max 999,999,999
- `quantity`: Required, non-negative integer, max 99,999
- `category`: Required, must be valid category
- `description`: Required, 10-500 characters

**Response (201 Created):**
```json
{
  "id": 43,
  "productName": "Samsung Galaxy S23",
  "price": 22000000,
  "quantity": 15,
  "category": "Điện tử",
  "description": "Flagship smartphone with advanced camera system and powerful processor"
}
```

**Response (400 Bad Request - Validation Error):**
```json
{
  "error": "Validation failed",
  "errors": [
    {
      "field": "productName",
      "message": "Product name must be between 3 and 100 characters"
    },
    {
      "field": "price",
      "message": "Price must be greater than 0"
    }
  ]
}
```

#### PUT /api/products/{id}
Update an existing product.

**Request Headers:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "productName": "Samsung Galaxy S23 Ultra",
  "price": 28000000,
  "quantity": 8,
  "category": "Điện tử",
  "description": "Premium flagship with S Pen and 200MP camera"
}
```

**Response (200 OK):**
```json
{
  "id": 43,
  "productName": "Samsung Galaxy S23 Ultra",
  "price": 28000000,
  "quantity": 8,
  "category": "Điện tử",
  "description": "Premium flagship with S Pen and 200MP camera"
}
```

#### DELETE /api/products/{id}
Delete a product by ID.

**Request Headers:**
```
Authorization: Bearer {jwt_token}
```

**Response (204 No Content):**
- No response body on successful deletion

**Response (404 Not Found):**
```json
{
  "error": "Product not found",
  "message": "Product with ID 999 does not exist"
}
```

---

## 🏗️ Architecture Deep Dive

### Backend Architecture Layers

**1. Controller Layer (REST API)**
- **Purpose**: Handle HTTP requests/responses
- **Responsibilities**:
  - Request validation
  - Route mapping
  - Response formatting
  - Exception handling
- **Key Classes**: `AuthController`, `ProductController`
- **Annotations**: `@RestController`, `@RequestMapping`, `@PostMapping`, `@GetMapping`

**2. Service Layer (Business Logic)**
- **Purpose**: Implement business rules and orchestration
- **Responsibilities**:
  - Business logic execution
  - Transaction management
  - Data transformation (Entity ↔ DTO)
  - External service integration
- **Key Classes**: `AuthService`, `ProductService`
- **Annotations**: `@Service`, `@Transactional`

**3. Repository Layer (Data Access)**
- **Purpose**: Database operations
- **Responsibilities**:
  - CRUD operations
  - Custom queries
  - Query method derivation
- **Key Interfaces**: `UserRepository`, `ProductRepository`
- **Technology**: Spring Data JPA with Hibernate

**4. Entity Layer (Domain Models)**
- **Purpose**: Represent database tables
- **Responsibilities**:
  - Data structure definition
  - Relationships mapping
  - JPA annotations
- **Key Classes**: `User`, `Product`
- **Annotations**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`

**5. DTO Layer (Data Transfer)**
- **Purpose**: Transfer data between layers
- **Responsibilities**:
  - Request/response data structure
  - Validation rules
  - Decoupling internal models from API
- **Key Classes**: `LoginRequest`, `LoginResponse`, `ProductDto`

### Frontend Architecture Components

**1. Components Layer**
- **Purpose**: UI rendering and user interaction
- **Key Files**:
  - `LoginForm.jsx`: Authentication UI
  - `ProductManagement.jsx`: Product CRUD interface
- **State Management**: React hooks (useState, useEffect)
- **Event Handling**: User interactions, form submissions

**2. Services Layer**
- **Purpose**: Backend API communication
- **Key Files**:
  - `authService.js`: Authentication API calls
  - `productService.js`: Product API calls
- **Technology**: Axios HTTP client
- **Features**: Request/response interceptors, error handling

**3. Utilities Layer**
- **Purpose**: Reusable helper functions
- **Key Files**:
  - `validation.js`: User input validation
  - `validateProduct.js`: Product data validation
- **Functions**: Email validation, password strength, product constraints

### Database Schema

**Users Table:**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_name (user_name),
    INDEX idx_email (email)
);
```

**Products Table:**
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL CHECK (price > 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    category VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_name (product_name),
    INDEX idx_category (category),
    INDEX idx_price (price),
    UNIQUE KEY uk_product_name (product_name)
);
```

### Security Configuration

**JWT Token Structure:**
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "admin",
  "iat": 1700000000,
  "exp": 1700086400
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret_key
)
```

**Password Hashing:**
- **Algorithm**: BCrypt
- **Strength**: 10 rounds (2^10 iterations)
- **Salt**: Automatically generated per password
- **Example**: `$2a$10$N9qo8uLOickgx2ZMRZoMye.UvHR5vQHh.ljZmVBWkJ7...`

---

## 🚀 Deployment Guide

### Development Environment

**Backend:**
```bash
# Start development server
cd backend
./mvnw spring-boot:run

# Server will start on http://localhost:8080
# Hot reload enabled via spring-boot-devtools
```

**Frontend:**
```bash
# Start development server
cd frontend
npm run dev

# App will start on http://localhost:5173
# Hot Module Replacement (HMR) enabled
```

### Production Build

**Backend:**
```bash
cd backend

# Build JAR file
./mvnw clean package

# JAR file created at: target/flogin-0.0.1-SNAPSHOT.jar

# Run production JAR
java -jar target/flogin-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend

# Build production bundle
npm run build

# Output in: dist/
# - Minified JavaScript
# - Optimized assets
# - Tree-shaken dependencies

# Preview production build
npm run preview
```

### Docker Deployment

**Backend Dockerfile:**
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Docker Compose:**
```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DATABASE_URL=jdbc:sqlserver://db:1433;databaseName=flogin
    depends_on:
      - db
  
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
  
  db:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      - ACCEPT_EULA=Y
      - SA_PASSWORD=YourStrong@Passw0rd
    ports:
      - "1433:1433"
    volumes:
      - db-data:/var/opt/mssql

volumes:
  db-data:
```

---

## 🐛 Troubleshooting Guide

### Common Backend Issues

**Issue 1: Port 8080 already in use**
```bash
# Find process using port
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows

# Or change port in application.properties
server.port=8081
```

**Issue 2: Database connection failed**
```properties
# Check application.properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=flogin
spring.datasource.username=sa
spring.datasource.password=your_password

# Verify SQL Server is running
# Test connection: telnet localhost 1433
```

**Issue 3: Maven build fails**
```bash
# Clear Maven cache
./mvnw clean

# Force update dependencies
./mvnw clean install -U

# Skip tests if needed
./mvnw clean package -DskipTests
```

### Common Frontend Issues

**Issue 1: npm install fails**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Try with legacy peer deps
npm install --legacy-peer-deps
```

**Issue 2: CORS errors**
```javascript
// Backend: Add CORS configuration in application.properties
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

// Or use @CrossOrigin annotation in controllers
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ProductController { ... }
```

**Issue 3: Vite build errors**
```bash
# Clear Vite cache
rm -rf node_modules/.vite

# Rebuild with clean slate
npm run build -- --force

# Check for TypeScript errors (if using TS)
npm run type-check
```

### Testing Issues

**Issue 1: Jest tests timeout**
```javascript
// Increase timeout in jest.config.cjs
module.exports = {
  testTimeout: 10000,  // 10 seconds
};

// Or in specific test
jest.setTimeout(10000);
```

**Issue 2: Cypress cannot find element**
```javascript
// Add explicit waits
cy.get('selector', { timeout: 10000 }).should('be.visible');

// Use cy.wait() for AJAX calls
cy.intercept('GET', '/api/products').as('getProducts');
cy.wait('@getProducts');
```

**Issue 3: Backend tests fail due to database**
```java
// Use @DataJpaTest for repository tests
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ProductRepositoryTest { ... }

// Or use @SpringBootTest with test profile
@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceTest { ... }
```

---

## 🔧 Configuration Reference

### Backend Configuration (application.properties)

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=flogin;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# Logging Configuration
logging.level.root=INFO
logging.level.com.flogin=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# JWT Configuration
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000

# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=Authorization,Content-Type
spring.web.cors.allow-credentials=true
```

### Frontend Configuration (vite.config.js)

```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    minify: 'terser',
    chunkSizeWarningLimit: 1000
  }
});
```
