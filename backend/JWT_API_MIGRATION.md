# JWT API Migration - jjwt 0.11.x → 0.12.x

## Vấn đề

Lỗi: `cannot resolve method parserBuilder`

## Nguyên nhân

JWT library (io.jsonwebtoken) phiên bản **0.12.3** đã thay đổi API so với phiên bản 0.11.x.

## Các thay đổi API chính

### 1. Parser API

#### ❌ API cũ (0.11.x):

```java
Jwts.parserBuilder()
    .setSigningKey(getSignKey())
    .build()
    .parseClaimsJws(token)
    .getBody();
```

#### ✅ API mới (0.12.x):

```java
Jwts.parser()
    .verifyWith(getSignKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

**Thay đổi:**

- `parserBuilder()` → `parser()`
- `setSigningKey()` → `verifyWith()`
- `parseClaimsJws()` → `parseSignedClaims()`
- `getBody()` → `getPayload()`

### 2. Builder API

#### ❌ API cũ (0.11.x):

```java
Jwts.builder()
    .setClaims(claims)
    .setSubject(subject)
    .setIssuedAt(now)
    .setExpiration(expiryDate)
    .signWith(getSignKey(), SignatureAlgorithm.HS256)
    .compact();
```

#### ✅ API mới (0.12.x):

```java
Jwts.builder()
    .claims(claims)
    .subject(subject)
    .issuedAt(now)
    .expiration(expiryDate)
    .signWith(getSignKey())
    .compact();
```

**Thay đổi:**

- `setClaims()` → `claims()`
- `setSubject()` → `subject()`
- `setIssuedAt()` → `issuedAt()`
- `setExpiration()` → `expiration()`
- `signWith(key, algorithm)` → `signWith(key)` (algorithm tự động detect)
- Không cần import `SignatureAlgorithm` nữa

## File đã cập nhật

### JwtService.java

**Phương thức đã sửa:**

1. **createToken()** - Builder API

   ```java
   private String createToken(Map<String, Object> claims, String subject) {
       Date now = new Date();
       Date expiryDate = new Date(now.getTime() + expiration);

       return Jwts.builder()
               .claims(claims)           // ✅ không có 'set' prefix
               .subject(subject)         // ✅ không có 'set' prefix
               .issuedAt(now)           // ✅ không có 'set' prefix
               .expiration(expiryDate)  // ✅ không có 'set' prefix
               .signWith(getSignKey())  // ✅ không cần SignatureAlgorithm
               .compact();
   }
   ```

2. **extractAllClaims()** - Parser API
   ```java
   private Claims extractAllClaims(String token) {
       return Jwts.parser()                    // ✅ parser() thay vì parserBuilder()
               .verifyWith(getSignKey())       // ✅ verifyWith() thay vì setSigningKey()
               .build()
               .parseSignedClaims(token)       // ✅ parseSignedClaims() thay vì parseClaimsJws()
               .getPayload();                  // ✅ getPayload() thay vì getBody()
   }
   ```

**Import đã xóa:**

```java
import io.jsonwebtoken.SignatureAlgorithm;  // ❌ Không cần nữa
```

## Dependencies trong pom.xml

```xml
<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Lợi ích của API mới

1. **Fluent API**: Tên methods ngắn gọn hơn, dễ đọc hơn
2. **Type Safety**: Auto-detect algorithm từ key type
3. **Consistency**: Naming convention thống nhất (không còn set/get prefix lộn xộn)
4. **Security**: `verifyWith()` rõ ràng hơn về mục đích verify signature

## Testing

Sau khi cập nhật, các methods public vẫn giữ nguyên signature:

- ✅ `generateToken(User user): String`
- ✅ `validateToken(String token, String username): Boolean`
- ✅ `extractUsername(String token): String`

→ Không cần thay đổi code ở `AuthService` hoặc tests.

## References

- [JJWT 0.12.0 Release Notes](https://github.com/jwtk/jjwt/releases/tag/0.12.0)
- [JJWT Documentation](https://github.com/jwtk/jjwt#install)
