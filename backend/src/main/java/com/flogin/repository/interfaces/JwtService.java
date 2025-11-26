package com.flogin.repository.interfaces;

import com.flogin.entity.User;

/**
 * JwtService - Interface for JWT Token Generation and Management
 * 
 * Purpose:
 * This interface defines the contract for JWT (JSON Web Token) operations
 * used in the authentication and authorization system. It abstracts the
 * JWT implementation details from the service layer.
 * 
 * JWT (JSON Web Token) Overview:
 * A JWT is a compact, URL-safe means of representing claims between two parties.
 * It consists of three parts separated by dots (.):  
 * Header.Payload.Signature
 * 
 * Example JWT Token:
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcwMDAwMDAwMH0.signature
 * 
 * JWT Structure:
 * 
 * 1. Header (Base64Url encoded):
 *    {
 *      "alg": "HS256",  // Algorithm: HMAC SHA256
 *      "typ": "JWT"      // Token type
 *    }
 * 
 * 2. Payload (Base64Url encoded - Claims):
 *    {
 *      "sub": "admin",           // Subject: username
 *      "email": "admin@example.com",
 *      "iat": 1700000000,         // Issued at timestamp
 *      "exp": 1700086400          // Expiration timestamp (24 hours later)
 *    }
 * 
 * 3. Signature (HMAC SHA256):
 *    HMACSHA256(
 *      base64UrlEncode(header) + "." + base64UrlEncode(payload),
 *      secret_key
 *    )
 * 
 * Method: generateToken(User user)
 * 
 * Purpose:
 * Generates a signed JWT token containing user information and expiration time.
 * This token is returned to the client after successful authentication and must
 * be included in subsequent requests to access protected resources.
 * 
 * Parameters:
 * - user: The authenticated User entity containing:
 *   * userName: Used as 'sub' (subject) claim
 *   * email: Additional user information
 *   * Other relevant user data
 * 
 * Returns:
 * - String: Complete JWT token in format Header.Payload.Signature
 * - Example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 * 
 * Token Claims (Payload Data):
 * - sub (Subject): User's username - identifies token owner
 * - iat (Issued At): Token creation timestamp
 * - exp (Expiration): Token expiration timestamp (typically +24 hours)
 * - email: User's email address
 * - Custom claims can be added as needed (roles, permissions, etc.)
 * 
 * Usage in Authentication Flow:
 * <pre>
 * {@code
 * @Service
 * public class AuthService {
 *     @Autowired
 *     private JwtService jwtService;
 *     
 *     public LoginResponse authenticate(LoginRequest request) {
 *         User user = userRepository.findByUserName(request.getUserName())
 *             .orElseThrow(() -> new AuthenticationException("User not found"));
 *         
 *         // Verify password (omitted)
 *         
 *         // Generate JWT token
 *         String token = jwtService.generateToken(user);
 *         
 *         UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
 *         return new LoginResponse(true, "Login successful", token, userDto);
 *     }
 * }
 * }
 * </pre>
 * 
 * Client Usage:
 * After receiving the token, client includes it in API requests:
 * <pre>
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * </pre>
 * 
 * Token Validation (Server-Side):
 * - Extract token from Authorization header
 * - Verify signature using secret key
 * - Check expiration time
 * - Extract user information from claims
 * - Grant access if valid, reject if invalid/expired
 * 
 * Implementation Considerations:
 * 
 * Secret Key:
 * - Must be kept secure (environment variable, config file)
 * - Should be strong and random (min 256 bits for HS256)
 * - Never commit to version control
 * - Rotate periodically for security
 * 
 * Token Expiration:
 * - Default: 24 hours (configurable)
 * - Shorter expiration = more secure, but more frequent re-authentication
 * - Longer expiration = better UX, but higher security risk
 * - Consider refresh tokens for long-lived sessions
 * 
 * Security Best Practices:
 * ✓ Use HTTPS only (prevent token interception)
 * ✓ Store token securely on client (HttpOnly cookie or secure storage)
 * ✓ Include expiration time (exp claim)
 * ✓ Use strong secret key (256+ bits)
 * ✓ Validate signature on every request
 * ✓ Consider token blacklist for logout
 * ✓ Don't store sensitive data in payload (it's Base64, not encrypted)
 * 
 * Token Refresh Strategy:
 * <pre>
 * {@code
 * // Refresh token before expiration
 * if (tokenExpiresIn < 1_HOUR) {
 *     String newToken = jwtService.generateToken(user);
 *     // Return new token to client
 * }
 * }
 * </pre>
 * 
 * Common JWT Libraries:
 * - io.jsonwebtoken:jjwt (Java JWT - commonly used)
 * - com.auth0:java-jwt (Auth0 JWT)
 * - org.springframework.security:spring-security-jwt
 * 
 * Example Implementation (using JJWT):
 * <pre>
 * {@code
 * @Service
 * public class JwtServiceImpl implements JwtService {
 *     @Value("${jwt.secret}")
 *     private String secret;
 *     
 *     @Value("${jwt.expiration:86400000}") // 24 hours
 *     private long expiration;
 *     
 *     @Override
 *     public String generateToken(User user) {
 *         Date now = new Date();
 *         Date expiryDate = new Date(now.getTime() + expiration);
 *         
 *         return Jwts.builder()
 *             .setSubject(user.getUserName())
 *             .claim("email", user.getEmail())
 *             .setIssuedAt(now)
 *             .setExpiration(expiryDate)
 *             .signWith(SignatureAlgorithm.HS256, secret)
 *             .compact();
 *     }
 * }
 * }
 * </pre>
 * 
 * @see User
 * @see com.flogin.service.AuthService
 * @see com.flogin.dto.LoginDto.LoginResponse
 */
public interface JwtService {
    String generateToken(User user);
}
