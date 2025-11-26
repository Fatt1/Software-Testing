package com.flogin.repository.interfaces;

import com.flogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Data Access Layer for User Entity
 * 
 * Purpose:
 * This repository interface provides database access methods for User entities
 * using Spring Data JPA. It handles all CRUD operations and custom queries
 * related to user management and authentication.
 * 
 * Design Pattern: Repository Pattern
 * - Abstracts database operations from business logic
 * - Provides a collection-like interface for domain objects
 * - Encapsulates data access logic
 * - Enables easy testing with mock repositories
 * 
 * Spring Data JPA Magic:
 * By extending JpaRepository, this interface automatically gets implementations for:
 * - save(User) - Create or update user
 * - findById(Long) - Find user by ID
 * - findAll() - Get all users
 * - delete(User) - Delete user
 * - count() - Count total users
 * - existsById(Long) - Check if user exists
 * And many more standard CRUD operations!
 * 
 * Generic Types:
 * JpaRepository<User, Long>
 * - User: The entity type this repository manages
 * - Long: The type of the entity's primary key (ID)
 * 
 * @Repository Annotation:
 * - Marks this as a Spring Data repository
 * - Enables exception translation (SQLException -> DataAccessException)
 * - Makes it eligible for component scanning
 * - Allows dependency injection
 * 
 * Custom Query Methods:
 * Spring Data JPA creates implementations based on method names:
 * 
 * 1. findByUserName(String userName)
 *    - Naming convention: findBy + PropertyName
 *    - Generated SQL: SELECT * FROM users WHERE user_name = ?
 *    - Returns Optional<User> (may or may not find user)
 *    - Used in authentication to look up user by login username
 * 
 * 2. findByUserNameIgnoreCase(String userName)
 *    - IgnoreCase keyword makes query case-insensitive
 *    - Generated SQL: SELECT * FROM users WHERE LOWER(user_name) = LOWER(?)
 *    - Useful for case-insensitive login
 *    - Example: "ADMIN", "admin", "Admin" all match same user
 * 
 * Method Name Keywords:
 * - findBy - Query method
 * - IgnoreCase - Case-insensitive comparison
 * - And - Combine conditions
 * - Or - Alternative conditions
 * - OrderBy - Sort results
 * - Top, First - Limit results
 * 
 * Usage Examples:
 * 
 * Example 1: Find user during login
 * <pre>
 * {@code
 * @Service
 * public class AuthService {
 *     @Autowired
 *     private UserRepository userRepository;
 *     
 *     public LoginResponse authenticate(LoginRequest request) {
 *         Optional<User> userOpt = userRepository.findByUserName(request.getUserName());
 *         
 *         if (userOpt.isEmpty()) {
 *             return new LoginResponse(false, "User not found");
 *         }
 *         
 *         User user = userOpt.get();
 *         // Verify password...
 *     }
 * }
 * }
 * </pre>
 * 
 * Example 2: Case-insensitive lookup
 * <pre>
 * {@code
 * // All these will find the same user
 * userRepository.findByUserNameIgnoreCase("admin");
 * userRepository.findByUserNameIgnoreCase("ADMIN");
 * userRepository.findByUserNameIgnoreCase("Admin");
 * }
 * </pre>
 * 
 * Example 3: Save new user
 * <pre>
 * {@code
 * User newUser = new User();
 * newUser.setUserName("newuser");
 * newUser.setEmail("user@example.com");
 * newUser.setPassword(passwordEncoder.encode("password123"));
 * 
 * User savedUser = userRepository.save(newUser);
 * // savedUser now has auto-generated ID
 * }
 * </pre>
 * 
 * Return Type: Optional<User>
 * - Optional is a container that may or may not contain a value
 * - Prevents NullPointerException
 * - Forces explicit handling of "not found" case
 * - Better than returning null
 * 
 * Optional Handling:
 * <pre>
 * {@code
 * // Method 1: Check if present
 * Optional<User> userOpt = userRepository.findByUserName("admin");
 * if (userOpt.isPresent()) {
 *     User user = userOpt.get();
 *     // Use user
 * }
 * 
 * // Method 2: Throw exception if not found
 * User user = userRepository.findByUserName("admin")
 *     .orElseThrow(() -> new UserNotFoundException("User not found"));
 * 
 * // Method 3: Provide default value
 * User user = userRepository.findByUserName("admin")
 *     .orElse(defaultUser);
 * }
 * </pre>
 * 
 * Database Table Mapping:
 * This repository works with the 'users' table:
 * - Table name defined by @Table(name = "users") in User entity
 * - Column names defined by @Column annotations
 * - Primary key is 'id' (auto-incremented BIGINT)
 * 
 * Transaction Management:
 * - JPA repository methods are transactional by default
 * - For custom @Query methods, add @Transactional if modifying data
 * - Read operations don't require explicit transaction
 * 
 * Testing:
 * <pre>
 * {@code
 * @DataJpaTest
 * class UserRepositoryTest {
 *     @Autowired
 *     private UserRepository userRepository;
 *     
 *     @Test
 *     void testFindByUserName() {
 *         User user = new User();
 *         user.setUserName("testuser");
 *         userRepository.save(user);
 *         
 *         Optional<User> found = userRepository.findByUserName("testuser");
 *         assertTrue(found.isPresent());
 *         assertEquals("testuser", found.get().getUserName());
 *     }
 * }
 * }
 * </pre>
 * 
 * Performance Considerations:
 * - Add database index on user_name column for faster queries
 * - Use pagination for listing large number of users
 * - Consider caching frequently accessed users
 * 
 * @see User
 * @see com.flogin.service.AuthService
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Tìm user theo username
     * Spring Data JPA tự động implement method này dựa trên naming convention
     * @param userName username cần tìm
     * @return Optional<User>
     */
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameIgnoreCase(String userName);
}
