package com.flogin.dto.UserDtos;

/**
 * UserDto - Data Transfer Object for User Information
 * 
 * Purpose:
 * This DTO represents user data sent to the client application. It provides
 * a safe, sanitized view of the User entity without exposing sensitive information.
 * 
 * Design Principle: Data Hiding
 * - Excludes sensitive fields (password, internal IDs, audit timestamps)
 * - Only exposes necessary user information for the UI
 * - Prevents over-posting and security vulnerabilities
 * - Decouples API from database schema
 * 
 * Use Cases:
 * 1. Login Response - Return user info after successful authentication
 * 2. User Profile - Display current user information
 * 3. User Management - List users in admin panel
 * 4. Comment/Post Author - Show user details with content
 * 
 * Fields:
 * 
 * userName (String)
 * - Unique username for login
 * - Display name in the UI
 * - Length: 3-50 characters
 * - Format: Alphanumeric with -, ., _
 * - Example: "admin", "john.doe", "user_123"
 * 
 * email (String)
 * - User's email address
 * - Used for notifications and password recovery
 * - Format: Valid email format (validated in entity)
 * - Example: "admin@example.com", "user@domain.com"
 * 
 * Fields NOT Included (for security):
 * - password / passwordHash - Never exposed to client
 * - id (database ID) - Internal reference only
 * - createdAt / updatedAt - Audit fields not needed by client
 * - roles / permissions - Handled separately by JWT claims
 * - lastLoginDate - Privacy consideration
 * 
 * Serialization:
 * This DTO is automatically serialized to JSON by Jackson:
 * <pre>
 * {
 *   "userName": "admin",
 *   "email": "admin@example.com"
 * }
 * </pre>
 * 
 * Deserialization:
 * Jackson can deserialize JSON back to UserDto (requires no-arg constructor):
 * <pre>
 * UserDto user = objectMapper.readValue(jsonString, UserDto.class);
 * </pre>
 * 
 * Conversion from Entity:
 * <pre>
 * {@code
 * // Manual conversion
 * User entity = userRepository.findById(id).orElseThrow();
 * UserDto dto = new UserDto(entity.getUserName(), entity.getEmail());
 * 
 * // Or using mapper (ModelMapper, MapStruct)
 * UserDto dto = modelMapper.map(entity, UserDto.class);
 * }
 * </pre>
 * 
 * Thread Safety:
 * This class is mutable (has setters) and therefore NOT thread-safe.
 * For concurrent access, consider using immutable builder pattern.
 * 
 * Validation:
 * Validation is performed at the Entity level, not DTO level, because:
 * - This DTO is for output (response), not input (request)
 * - Input validation uses CreateUserRequest/UpdateUserRequest
 * - DTOs for output don't need validation annotations
 * 
 * Best Practices:
 * ✓ Keep DTOs simple (data only, no business logic)
 * ✓ Use separate DTOs for different use cases (request vs response)
 * ✓ Never expose sensitive data through DTOs
 * ✓ Version DTOs to maintain API compatibility
 * ✓ Document field meanings and constraints
 * 
 * Example Usage in Service:
 * <pre>
 * {@code
 * public LoginResponse authenticate(LoginRequest request) {
 *     User user = userRepository.findByUserName(request.getUserName())
 *         .orElseThrow(() -> new AuthenticationException("User not found"));
 *     
 *     // Verify password...
 *     
 *     UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
 *     String token = jwtService.generateToken(user);
 *     
 *     return new LoginResponse(true, "Login successful", token, userDto);
 * }
 * }
 * </pre>
 * 
 * @see com.flogin.entity.User
 * @see LoginResponse
 */
public class UserDto {
  private String userName;
  private String email;

  public UserDto() {
  }
  public UserDto(String userName, String email) {
    this.userName = userName;
    this.email = email;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  
    
}
