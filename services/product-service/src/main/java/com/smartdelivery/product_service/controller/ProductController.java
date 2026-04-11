package com.smartdelivery.product_service.controller;

import com.smartdelivery.product_service.dto.*;
import com.smartdelivery.product_service.entity.SkinType;
import com.smartdelivery.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Categories
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getCategoryBySlug(slug));
    }

    // Products - read
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) SkinType skinType,
            @RequestParam(required = false) Boolean inStock
    ) {
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
        }
        if (brand != null) {
            return ResponseEntity.ok(productService.getProductsByBrand(brand));
        }
        if (skinType != null) {
            return ResponseEntity.ok(productService.getProductsBySkinType(skinType));
        }
        if (Boolean.TRUE.equals(inStock)) {
            return ResponseEntity.ok(productService.getProductsInStock());
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Products - write
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // Stocks
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    @PatchMapping("/{id}/stock/decrement")
    public ResponseEntity<ProductResponse> decrementStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(productService.decrementStock(id, quantity));
    }

    @PatchMapping("/{id}/stock/restore")
    public ResponseEntity<ProductResponse> restoreStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(productService.restoreStock(id, quantity));
    }
}