package com.flogin.service;

import com.flogin.dto.ProductDtos.ProductDto;
import com.flogin.entity.Product;
import com.flogin.repository.interfaces.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductDto createProduct(ProductDto productDto) {
        // Chưa viết hàm validate product

        Product product = new Product(1,
                productDto.getCategory(),
                productDto.getDescription(),
                productDto.getQuantity(),
                productDto.getProductName(),
                productDto.getPrice());

        productDto.setId(product.getId());
        return productDto;
    }

    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        // Map từ product sang ProductDTO
        return productPage.map(this::toDto);
    }

    public void deleteProduct(long id) {
        // Chưa viết validation
        productRepository.deleteById(id);
    }

    public ProductDto getProduct(long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Viết thêm exception not found nữa"));
        return toDto(product);
    }

    public  ProductDto updateProduct(ProductDto productDto) {
        Product existingProduct = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productDto.getId()));
        // 2. Cập nhật các trường của sản phẩm cũ từ DTO

        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setCategory(productDto.getCategory());

        // 3. Lưu lại sản phẩm đã cập nhật
        Product updatedProduct = productRepository.save(existingProduct);
        return toDto(updatedProduct);
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
