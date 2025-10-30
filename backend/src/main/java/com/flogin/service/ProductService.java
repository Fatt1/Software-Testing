package com.flogin.service;

import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private static final List<String> VALID_CATEGORIES = Collections.unmodifiableList(
            Arrays.asList("Electronics", "Books", "Clothing", "Toys", "Groceries")
    );
    public ProductDto createProduct(ProductDto productDto) {
        // 1. Validate DTO đầu vào
        List<String> errors = validateProduct(productDto);
        if (!errors.isEmpty()) {
            // Nếu có lỗi, ném exception với danh sách lỗi
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Product product = new Product(1L,
                productDto.getCategory(),
                productDto.getDescription(),
                productDto.getQuantity(),
                productDto.getProductName(),
                productDto.getPrice());
        Product savedProduct =  productRepository.save(product);

        return toDto(savedProduct);
    }

    public Page<ProductDto> getAll(Pageable pageable) {

        Page<Product> productPage = productRepository.findAll(pageable);

        // Map từ product sang ProductDTO
        return productPage.map(this::toDto);
    }

    public void deleteProduct(long id) {
        // Chưa viết validation
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        productRepository.deleteById(product);

    }

    public ProductDto getProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return toDto(product);
    }


    public  ProductDto updateProduct(ProductDto productDto) {

        // 1. Validate DTO đầu vào
        List<String> errors = validateProduct(productDto);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Product existingProduct = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productDto.getId()));


        // 2. Cập nhật các trường của sản phẩm cũ từ DTO

        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setCategory(productDto.getCategory());

        // 3. Lưu lại sản phẩm đã cập nhật
        Product updatedProduct = productRepository.save(existingProduct);
        return toDto(updatedProduct);
    }


    private List<String> validateProduct(ProductDto dto) {
        List<String> errors = new ArrayList<>();

        // 1. Product Name: 3-100 ký tự, không rỗng
        String name = dto.getProductName();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Product Name không được rỗng");
        } else if (name.length() < 3 || name.length() > 100) {
            errors.add("Product Name phải từ 3 đến 100 ký tự");
        }

        // 2. Price: > 0, <= 999,999,999 (Đã đổi sang Double)
        Double price = dto.getPrice(); // Giả định dto.getPrice() trả về Double
        if (price == null) {
            errors.add("Price không được để trống");
        } else {
            // Sử dụng toán tử so sánh thông thường cho double
            if (price <= 0.0) {
                errors.add("Price phải > 0");
            }

            // So sánh trực tiếp với giá trị 999999999.0
            if (price > 999999999.0) {
                errors.add("Price không được vượt quá 999,999,999");
            }
        }

        // 3. Quantity: >= 0, <= 99,999
        Integer quantity = dto.getQuantity();
        if (quantity == null) {
            errors.add("Quantity không được để trống");
        } else {
            if (quantity < 0) {
                errors.add("Quantity phải >= 0");
            }
            if (quantity > 99999) {
                errors.add("Quantity không được vượt quá 99,999");
            }
        }

        // 4. Description: <= 500 ký tự
        String description = dto.getDescription();
        if (description != null && description.trim().length() > 500) {
            errors.add("Description không được quá 500 ký tự");
        }

        // 5. Category: Phải thuộc danh sách categories có sẵn
        String category = dto.getCategory();
        if (category == null || category.trim().isEmpty()) {
            errors.add("Category không được rỗng");
        } else if (!VALID_CATEGORIES.contains(category)) {
            // Kiểm tra với danh sách VALID_CATEGORIES thay vì repository
            errors.add("Category '" + category + "' không hợp lệ.");
        }

        return errors;
    }


    /**
     * Chuyển đổi từ Product (Entity) sang ProductDto (DTO).
     */
    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getCategory(),
                product.getPrice(),
                product.getProductName(),
                product.getDescription(),
                product.getQuantity()
        );

    }
    }


