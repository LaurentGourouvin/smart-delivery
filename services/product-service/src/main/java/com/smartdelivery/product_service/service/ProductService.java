package com.smartdelivery.product_service.service;

import com.smartdelivery.product_service.dto.*;
import com.smartdelivery.product_service.entity.Category;
import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import com.smartdelivery.product_service.exception.CategoryNotFoundException;
import com.smartdelivery.product_service.exception.ProductNotFoundException;
import com.smartdelivery.product_service.repository.CategoryRepository;
import com.smartdelivery.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Categories
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::toCategoryResponse)
                .orElseThrow(() -> new CategoryNotFoundException(slug));
    }

    // Products - read
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(this::toProductResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrand(String brand) {
        return productRepository.findByBrandAndActiveTrue(brand)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySkinType(SkinType skinType) {
        return productRepository.findBySkinTypeAndActiveTrue(skinType)
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsInStock() {
        return productRepository.findAllInStock()
                .stream()
                .map(this::toProductResponse)
                .toList();
    }

    // Products - write
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId().toString()));

        Product product = Product.builder()
                .category(category)
                .name(request.name())
                .description(request.description())
                .brand(request.brand())
                .price(request.price())
                .stock(request.stock() != null ? request.stock() : 0)
                .imageUrl(request.imageUrl())
                .skinType(request.skinType() != null ? request.skinType() : SkinType.ALL)
                .volumeMl(request.volumeMl())
                .build();

        return toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId().toString()));
            product.setCategory(category);
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());

        if (request.skinType() != null) product.setSkinType(request.skinType());
        if (request.volumeMl() != null) product.setVolumeMl(request.volumeMl());
        if (request.active() != null) product.setActive(request.active());

        return toProductResponse(productRepository.save(product));
    }

    // Stock — Optimistic Lock
    @Transactional
    public ProductResponse updateStock(UUID id, UpdateStockRequest request) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));

            product.setStock(request.quantity());
            return toProductResponse(productRepository.save(product));

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Stock update conflict — please retry", e);
        }
    }

    @Transactional
    public ProductResponse decrementStock(UUID id, Integer quantity) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));

            if (product.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + id);
            }

            product.setStock(product.getStock() - quantity);
            return toProductResponse(productRepository.save(product));

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Stock conflict — please retry", e);
        }
    }

    @Transactional
    public ProductResponse restoreStock(UUID id, Integer quantity) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));

            product.setStock(product.getStock() + quantity);
            return toProductResponse(productRepository.save(product));

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Stock conflict — please retry", e);
        }
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getSkinType(),
                product.getVolumeMl(),
                product.getActive()
        );
    }

}
