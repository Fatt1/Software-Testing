package com.flogin.repository.interfaces;

import com.flogin.entity.Category;
import com.flogin.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository - Data Access Layer for Product Entity
 * 
 * Purpose:
 * This repository interface provides comprehensive database access methods for
 * Product entities, including CRUD operations, searching, filtering, and
 * business rule validation queries.
 * 
 * Design Pattern: Repository Pattern
 * - Encapsulates data access logic
 * - Provides domain-specific queries
 * - Abstracts database implementation details
 * - Enables easy unit testing with mocks
 * 
 * Spring Data JPA Features:
 * By extending JpaRepository<Product, Long>, this interface automatically provides:
 * - save(Product) - Create or update product
 * - findById(Long) - Find product by ID
 * - findAll() - Get all products
 * - findAll(Pageable) - Get paginated products
 * - delete(Product) - Delete product
 * - deleteById(Long) - Delete by ID
 * - count() - Count total products
 * - existsById(Long) - Check existence
 * 
 * Custom Query Methods:
 * Spring Data JPA generates SQL from method names following naming conventions:
 * 
 * 1. findByCategory(Category category)
 *    Query: SELECT * FROM products WHERE category = ?
 *    Purpose: Filter products by category (Điện tử, Thời trang, etc.)
 *    Returns: List<Product> - All products in specified category
 *    
 *    Usage:
 *    <pre>
 *    {@code
 *    List<Product> electronics = productRepository.findByCategory(Category.ELECTRONICS);
 *    // Returns all products in Electronics category
 *    }
 *    </pre>
 * 
 * 2. findByProductNameContaining(String keyword)
 *    Query: SELECT * FROM products WHERE product_name LIKE %keyword%
 *    Purpose: Search products by name (case-sensitive substring match)
 *    Returns: List<Product> - Products whose name contains the keyword
 *    
 *    Usage:
 *    <pre>
 *    {@code
 *    // Search for products with "laptop" in name
 *    List<Product> results = productRepository.findByProductNameContaining("laptop");
 *    // Finds: "Laptop Dell", "Gaming Laptop", "Laptop Asus"
 *    }
 *    </pre>
 * 
 * 3. findByPriceBetween(double minPrice, double maxPrice)
 *    Query: SELECT * FROM products WHERE price BETWEEN minPrice AND maxPrice
 *    Purpose: Filter products by price range
 *    Returns: List<Product> - Products within specified price range (inclusive)
 *    
 *    Usage:
 *    <pre>
 *    {@code
 *    // Find products between 10 million and 30 million VND
 *    List<Product> midRange = productRepository.findByPriceBetween(10000000, 30000000);
 *    }
 *    </pre>
 * 
 * 4. existsByProductName(String productName)
 *    Query: SELECT COUNT(*) > 0 FROM products WHERE product_name = ?
 *    Purpose: Check if product name already exists (for duplicate prevention)
 *    Returns: boolean - true if product name exists, false otherwise
 *    
 *    Usage:
 *    <pre>
 *    {@code
 *    // Before creating new product
 *    if (productRepository.existsByProductName("iPhone 15")) {
 *        throw new DuplicateProductException("Product name already exists");
 *    }
 *    }
 *    </pre>
 * 
 * 5. existsByProductNameAndIdNot(String productName, Long id)
 *    Query: SELECT COUNT(*) > 0 FROM products 
 *           WHERE product_name = ? AND id != ?
 *    Purpose: Check duplicate name during update (exclude current product)
 *    Returns: boolean - true if another product has this name
 *    
 *    Usage:
 *    <pre>
 *    {@code
 *    // When updating product with id=123
 *    if (productRepository.existsByProductNameAndIdNot(newName, 123L)) {
 *        throw new DuplicateProductException("Another product has this name");
 *    }
 *    // Allows keeping same name when updating other fields
 *    }
 *    </pre>
 * 
 * Method Naming Convention Keywords:
 * - findBy - Start query method
 * - Containing - LIKE %value%
 * - Between - BETWEEN x AND y
 * - existsBy - Returns boolean
 * - And - Combine conditions
 * - Or - Alternative conditions
 * - Not - Negation
 * - OrderBy - Sort results
 * - IgnoreCase - Case-insensitive
 * 
 * Advanced Query Examples (can be added if needed):
 * <pre>
 * {@code
 * // Find by category and price range
 * List<Product> findByCategoryAndPriceLessThan(Category category, double maxPrice);
 * 
 * // Find by name (case-insensitive)
 * List<Product> findByProductNameContainingIgnoreCase(String keyword);
 * 
 * // Find top 10 products ordered by price
 * List<Product> findTop10ByOrderByPriceDesc();
 * 
 * // Find products with quantity less than threshold
 * List<Product> findByQuantityLessThan(int threshold);
 * 
 * // Paginated search
 * Page<Product> findByProductNameContaining(String keyword, Pageable pageable);
 * }
 * </pre>
 * 
 * Pagination Support:
 * <pre>
 * {@code
 * // Get page 0 (first page) with 10 items per page
 * Pageable pageable = PageRequest.of(0, 10, Sort.by("productName"));
 * Page<Product> page = productRepository.findAll(pageable);
 * 
 * // Page contains:
 * List<Product> content = page.getContent(); // Products in this page
 * int totalPages = page.getTotalPages();
 * long totalElements = page.getTotalElements();
 * boolean hasNext = page.hasNext();
 * }
 * </pre>
 * 
 * Transaction Management:
 * - Read operations (findBy*, existsBy*) are read-only transactions
 * - Write operations (save, delete) are transactional
 * - For custom @Query with modifications, add @Modifying @Transactional
 * 
 * Testing:
 * <pre>
 * {@code
 * @DataJpaTest
 * class ProductRepositoryTest {
 *     @Autowired
 *     private ProductRepository productRepository;
 *     
 *     @Test
 *     void testFindByCategory() {
 *         Product product = new Product();
 *         product.setProductName("Test Laptop");
 *         product.setCategory(Category.ELECTRONICS);
 *         productRepository.save(product);
 *         
 *         List<Product> found = productRepository.findByCategory(Category.ELECTRONICS);
 *         assertEquals(1, found.size());
 *     }
 * }
 * }
 * </pre>
 * 
 * Performance Optimization:
 * - Add database index on: product_name, category, price
 * - Use pagination for large result sets
 * - Consider query caching for frequently accessed data
 * - Use @Query with JOIN FETCH for eager loading relationships
 * 
 * @see Product
 * @see Category
 * @see com.flogin.service.ProductService
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    
    List<Product> findByCategory(Category category);
    
    /**
     * Tìm products theo product name (chứa keyword)
     */
    List<Product> findByProductNameContaining(String keyword);
    
    /**
     * Tìm products có giá trong khoảng
     */
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

     // Thêm method kiểm tra tên sản phẩm đã tồn tại
    boolean existsByProductName(String productName);

     //Thêm method kiểm tra tên trùng nhưng không phải chính sản phẩm đó (cho update)
    boolean existsByProductNameAndIdNot(String productName, Long id);
}
