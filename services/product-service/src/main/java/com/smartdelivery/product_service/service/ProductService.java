package com.smartdelivery.product_service.service;

import com.smartdelivery.product_service.dto.*;
import com.smartdelivery.product_service.entity.Category;
import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import com.smartdelivery.product_service.entity.StockRestoration;
import com.smartdelivery.product_service.event.OrderItemEvent;
import com.smartdelivery.product_service.event.PaymentFailedEvent;
import com.smartdelivery.product_service.event.StockRestoredEvent;
import com.smartdelivery.product_service.exception.CategoryNotFoundException;
import com.smartdelivery.product_service.exception.ProductNotFoundException;
import com.smartdelivery.product_service.repository.CategoryRepository;
import com.smartdelivery.product_service.repository.ProductRepository;
import com.smartdelivery.product_service.repository.StockRestorationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRestorationRepository stockRestorationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_STOCK_RESTORED = "stock.restored";

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

    /**
     * Recherche multicritère : tous les filtres sont optionnels et combinables.
     * Un appel sans aucun filtre retourne l'ensemble des produits actifs.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(UUID categoryId,
                                                String brand,
                                                SkinType skinType,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice,
                                                Boolean inStock) {
        return productRepository
                .search(categoryId, brand, skinType, minPrice, maxPrice, inStock)
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

    // ── Saga : compensation du décrément de stock ──────────────────────

    /**
     * Paiement refusé → le stock réservé par la commande est restitué.
     *
     * product-service réagit au même fait que order-service, sans le connaître :
     * l'un annule la commande, l'autre rend le stock.
     *
     * Idempotence garantie par la table stock_restorations, dont la clé primaire
     * est l'orderId. Kafka livrant at-least-once, un événement relivré ferait
     * sinon remonter le stock une seconde fois.
     */
    @KafkaListener(topics = "payment.failed", groupId = "product-service")
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment.failed for order {}", event.getOrderId());

        if (stockRestorationRepository.existsById(event.getOrderId())) {
            log.warn("Stock already restored for order {} — skipping", event.getOrderId());
            return;
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            log.warn("No items in payment.failed for order {} — nothing to restore",
                    event.getOrderId());
            return;
        }

        for (OrderItemEvent item : event.getItems()) {
            restoreStock(item.getProductId(), item.getQuantity());
            log.info("Restored {} unit(s) of product {}", item.getQuantity(), item.getProductId());
        }

        // Marqueur écrit dans la même transaction que les restitutions :
        // soit tout est appliqué, soit rien ne l'est.
        stockRestorationRepository.save(
                StockRestoration.builder().orderId(event.getOrderId()).build());

        StockRestoredEvent restored = StockRestoredEvent.builder()
                .orderId(event.getOrderId())
                .items(event.getItems())
                .build();

        kafkaTemplate.send(TOPIC_STOCK_RESTORED, event.getOrderId().toString(), restored);
        log.info("Stock restored for order {} — {} item(s), published stock.restored",
                event.getOrderId(), event.getItems().size());
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
