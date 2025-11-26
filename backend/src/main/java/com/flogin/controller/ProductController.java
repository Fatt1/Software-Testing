package com.flogin.controller;

import com.flogin.dto.ProductDtos.CreateProductRequest;
import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.dto.ProductDtos.UpdateProductRequest;
import com.flogin.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * ProductController - REST API Controller for Product Management
 * 
 * <p>Controller này quản lý tất cả các operations liên quan đến Product,
 * bao gồm CRUD (Create, Read, Update, Delete) operations với database.</p>
 * 
 * <p>Các endpoint chính:</p>
 * <ul>
 *   <li>POST /api/products - Tạo product mới</li>
 *   <li>GET /api/products - Lấy danh sách products với pagination</li>
 *   <li>GET /api/products/{id} - Lấy chi tiết một product</li>
 *   <li>PUT /api/products/{id} - Cập nhật thông tin product</li>
 *   <li>DELETE /api/products/{id} - Xóa product khỏi hệ thống</li>
 * </ul>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Pagination support cho danh sách products</li>
 *   <li>Input validation với @Valid annotation</li>
 *   <li>RESTful design với proper HTTP status codes</li>
 *   <li>CORS enabled cho frontend integration</li>
 * </ul>
 * 
 * @author Software Testing Team
 * @version 1.0
 * @since 2025-11-26
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    
    /**
     * ProductService instance được inject bởi Spring để xử lý
     * business logic của product management operations
     */
    @Autowired
    private ProductService productService;

    /**
     * Endpoint tạo product mới trong hệ thống
     * 
     * <p>Nhận thông tin product từ request body, validate input,
     * lưu vào database và trả về product đã được tạo.</p>
     * 
     * <p><b>Request Body Example:</b></p>
     * <pre>
     * {
     *   "name": "iPhone 15 Pro",
     *   "description": "Latest iPhone model",
     *   "price": 999.99,
     *   "quantity": 50
     * }
     * </pre>
     * 
     * <p><b>Success Response Example (201 Created):</b></p>
     * <pre>
     * {
     *   "id": 1,
     *   "name": "iPhone 15 Pro",
     *   "description": "Latest iPhone model",
     *   "price": 999.99,
     *   "quantity": 50,
     *   "createdAt": "2025-11-26T10:30:00"
     * }
     * </pre>
     * 
     * @param request CreateProductRequest chứa thông tin product cần tạo
     * @return ResponseEntity với ProductDto đã được tạo và HTTP status 201 Created
     * @throws org.springframework.web.bind.MethodArgumentNotValidException nếu validation fails
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
            ProductDto createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

    /**
     * Endpoint lấy danh sách tất cả products với pagination support
     * 
     * <p>Trả về danh sách products theo trang, giúp tối ưu performance
     * khi số lượng products lớn. Client có thể control page number và page size.</p>
     * 
     * <p><b>Request Example:</b></p>
     * <pre>GET /api/products?page=0&size=10</pre>
     * 
     * <p><b>Success Response Example (200 OK):</b></p>
     * <pre>
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "name": "iPhone 15 Pro",
     *       "price": 999.99,
     *       "quantity": 50
     *     },
     *     ...
     *   ],
     *   "pageable": {
     *     "pageNumber": 0,
     *     "pageSize": 10
     *   },
     *   "totalElements": 100,
     *   "totalPages": 10
     * }
     * </pre>
     * 
     * @param page Số trang cần lấy (zero-based index, mặc định = 0)
     * @param size Số lượng items mỗi trang (mặc định = 10)
     * @return ResponseEntity chứa Page<ProductDto> với HTTP status 200 OK
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Tạo Pageable object từ page và size parameters
        Pageable pageable = PageRequest.of(page, size);
        // Gọi service để lấy products với pagination
        Page<ProductDto> products = productService.getAll(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint lấy thông tin chi tiết của một product theo ID
     * 
     * <p>Tìm kiếm product trong database theo ID và trả về
     * thông tin đầy đủ của product đó.</p>
     * 
     * <p><b>Request Example:</b></p>
     * <pre>GET /api/products/1</pre>
     * 
     * <p><b>Success Response Example (200 OK):</b></p>
     * <pre>
     * {
     *   "id": 1,
     *   "name": "iPhone 15 Pro",
     *   "description": "Latest iPhone model",
     *   "price": 999.99,
     *   "quantity": 50,
     *   "createdAt": "2025-11-26T10:30:00"
     * }
     * </pre>
     * 
     * @param id ID của product cần lấy thông tin (Long type)
     * @return ResponseEntity với ProductDto và HTTP status 200 OK
     * @throws NoSuchElementException nếu không tìm thấy product với ID đã cho
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {
            // Gọi service để tìm product theo ID
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);

    }

    /**
     * Endpoint cập nhật thông tin product existing
     * 
     * <p>Nhận ID của product và thông tin mới cần cập nhật,
     * validate input, update database và trả về product đã được cập nhật.</p>
     * 
     * <p><b>Request Body Example:</b></p>
     * <pre>
     * PUT /api/products/1
     * {
     *   "name": "iPhone 15 Pro Max",
     *   "description": "Updated description",
     *   "price": 1099.99,
     *   "quantity": 30
     * }
     * </pre>
     * 
     * <p><b>Success Response Example (200 OK):</b></p>
     * <pre>
     * {
     *   "id": 1,
     *   "name": "iPhone 15 Pro Max",
     *   "description": "Updated description",
     *   "price": 1099.99,
     *   "quantity": 30,
     *   "updatedAt": "2025-11-26T11:00:00"
     * }
     * </pre>
     * 
     * @param id ID của product cần cập nhật (Long type)
     * @param request UpdateProductRequest chứa thông tin mới cần update
     * @return ResponseEntity với ProductDto đã được cập nhật và HTTP status 200 OK
     * @throws NoSuchElementException nếu không tìm thấy product với ID đã cho
     * @throws org.springframework.web.bind.MethodArgumentNotValidException nếu validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id, 
            @Valid @RequestBody UpdateProductRequest request) {
            // Gọi service để update product với thông tin mới
            ProductDto updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);

    }

    /**
     * Endpoint xóa product khỏi hệ thống
     * 
     * <p>Tìm product theo ID và xóa khỏi database.
     * Trả về 204 No Content nếu xóa thành công (no response body needed).</p>
     * 
     * <p><b>Request Example:</b></p>
     * <pre>DELETE /api/products/1</pre>
     * 
     * <p><b>Success Response:</b> 204 No Content (empty body)</p>
     * 
     * <p><b>Use Cases:</b></p>
     * <ul>
     *   <li>Xóa products không còn kinh doanh</li>
     *   <li>Cleanup data trong testing</li>
     *   <li>Remove discontinued items</li>
     * </ul>
     * 
     * <p><b>Note:</b> Operation này là permanent và không thể undo.
     * Consider implementing soft delete nếu cần maintain history.</p>
     * 
     * @param id ID của product cần xóa (Long type)
     * @return ResponseEntity với HTTP status 204 No Content nếu thành công
     * @throws NoSuchElementException nếu không tìm thấy product với ID đã cho
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
            // Gọi service để xóa product khỏi database
            productService.deleteProduct(id);
            // Trả về 204 No Content để indicate successful deletion
            return ResponseEntity.noContent().build();

    }
}
