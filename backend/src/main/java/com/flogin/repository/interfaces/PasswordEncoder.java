package com.flogin.repository.interfaces;

/**
 * PasswordEncoder - Interface for Password Hashing and Verification
 * 
 * Purpose:
 * This interface defines the contract for password encoding and verification
 * operations. It abstracts the password hashing implementation (typically BCrypt)
 * from the service layer, enabling secure password storage and authentication.
 * 
 * Security Principle: Never Store Plain-Text Passwords
 * - Passwords must NEVER be stored in plain text in the database
 * - Always hash passwords before storage using strong algorithms
 * - Use one-way hashing (irreversible) with salt
 * - BCrypt is the industry standard for password hashing
 * 
 * Method: matches(CharSequence rawPassword, String encodedPassword)
 * 
 * Purpose:
 * Verifies if a plain-text password matches a previously hashed password.
 * This is used during login to authenticate users.
 * 
 * Parameters:
 * 
 * 1. rawPassword (CharSequence):
 *    - The plain-text password entered by user during login
 *    - CharSequence allows String, StringBuilder, char[], etc.
 *    - Example: "admin123" (what user types)
 *    - Never stored, only used for comparison
 * 
 * 2. encodedPassword (String):
 *    - The hashed password stored in database
 *    - Generated using BCrypt algorithm
 *    - Format: "$2a$10$<salt><hash>" (60 characters)
 *    - Example: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
 *    - Retrieved from User entity's password field
 * 
 * Returns:
 * - boolean: true if passwords match, false otherwise
 * - true: User provided correct password (authentication successful)
 * - false: User provided wrong password (authentication failed)
 * 
 * How BCrypt Works:
 * 
 * 1. Hashing (Registration/Password Change):
 *    rawPassword: "admin123"
 *    + Random Salt: "$2a$10$N9qo8uLOickgx2ZMRZoMye"
 *    = Hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
 *    
 * 2. Verification (Login):
 *    Extract salt from stored hash
 *    Hash input password with same salt
 *    Compare result with stored hash
 *    Return true if match, false otherwise
 * 
 * BCrypt Format Breakdown:
 * "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
 *  |||  ||  |                    |                                  |
 *  |||  ||  |                    |                                  +- Hash (31 chars)
 *  |||  ||  |                    +- Salt (22 chars)
 *  |||  ||  +- Salt prefix
 *  |||  +- Cost factor (10 = 2^10 = 1024 rounds)
 *  ||+- Minor version (a)
 *  |+- Algorithm identifier (2)
 *  +- Prefix
 * 
 * Cost Factor (Work Factor):
 * - Default: 10 (2^10 = 1024 rounds)
 * - Higher = more secure but slower
 * - Lower = faster but less secure
 * - Should increase over time as hardware improves
 * - Range: 4-31 (practical: 10-12)
 * 
 * Usage in Authentication:
 * <pre>
 * {@code
 * @Service
 * public class AuthService {
 *     @Autowired
 *     private UserRepository userRepository;
 *     
 *     @Autowired
 *     private PasswordEncoder passwordEncoder;
 *     
 *     public LoginResponse authenticate(LoginRequest request) {
 *         // 1. Find user by username
 *         User user = userRepository.findByUserName(request.getUserName())
 *             .orElseThrow(() -> new AuthenticationException("User not found"));
 *         
 *         // 2. Verify password
 *         String rawPassword = request.getPassword();        // "admin123"
 *         String encodedPassword = user.getPassword();       // "$2a$10$..."
 *         
 *         boolean passwordMatches = passwordEncoder.matches(rawPassword, encodedPassword);
 *         
 *         if (!passwordMatches) {
 *             return new LoginResponse(false, "Invalid password");
 *         }
 *         
 *         // 3. Generate token and return success
 *         String token = jwtService.generateToken(user);
 *         return new LoginResponse(true, "Login successful", token);
 *     }
 * }
 * }
 * </pre>
 * 
 * Password Encoding (Registration):
 * <pre>
 * {@code
 * @Service
 * public class UserService {
 *     @Autowired
 *     private PasswordEncoder passwordEncoder;
 *     
 *     public void createUser(CreateUserRequest request) {
 *         User user = new User();
 *         user.setUserName(request.getUserName());
 *         
 *         // Hash password before storing
 *         String encodedPassword = passwordEncoder.encode(request.getPassword());
 *         user.setPassword(encodedPassword);
 *         
 *         userRepository.save(user);
 *     }
 * }
 * }
 * </pre>
 * 
 * Why Not Just Hash?
 * Simple hashing (MD5, SHA) is NOT secure for passwords:
 * ✗ No salt = same password = same hash (rainbow table attack)
 * ✗ Too fast = easy to brute force
 * ✗ No work factor = can't adapt to faster hardware
 * 
 * Why BCrypt?
 * ✓ Includes random salt (unique hash per password)
 * ✓ Slow by design (resistant to brute force)
 * ✓ Adaptive (work factor can increase over time)
 * ✓ Industry standard (well-tested and trusted)
 * 
 * Security Best Practices:
 * ✓ Always use BCrypt (or Argon2, scrypt)
 * ✓ Never implement your own crypto
 * ✓ Use cost factor 10-12 (balance security vs performance)
 * ✓ Never log passwords (raw or hashed)
 * ✓ Use HTTPS to prevent password interception
 * ✓ Implement rate limiting on login attempts
 * ✓ Consider password policies (min length, complexity)
 * 
 * Implementation (Spring Security):
 * <pre>
 * {@code
 * @Configuration
 * public class SecurityConfig {
 *     @Bean
 *     public PasswordEncoder passwordEncoder() {
 *         return new BCryptPasswordEncoder(10); // Cost factor 10
 *     }
 * }
 * }
 * </pre>
 * 
 * Performance Consideration:
 * - BCrypt is intentionally slow (~100-300ms per hash)
 * - This is a FEATURE, not a bug
 * - Prevents rapid brute-force attacks
 * - Not a problem for normal login (few times per session)
 * - Don't use for high-frequency operations
 * 
 * @see User
 * @see com.flogin.service.AuthService
 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
public interface PasswordEncoder {
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
