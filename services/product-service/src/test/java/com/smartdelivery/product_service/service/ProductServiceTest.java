package com.smartdelivery.product_service.service;

import com.smartdelivery.product_service.dto.CreateProductRequest;
import com.smartdelivery.product_service.dto.ProductResponse;
import com.smartdelivery.product_service.entity.Category;
import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import com.smartdelivery.product_service.exception.CategoryNotFoundException;
import com.smartdelivery.product_service.exception.ProductNotFoundException;
import com.smartdelivery.product_service.event.OrderItemEvent;
import com.smartdelivery.product_service.event.PaymentFailedEvent;
import com.smartdelivery.product_service.repository.CategoryRepository;
import com.smartdelivery.product_service.repository.ProductRepository;
import com.smartdelivery.product_service.repository.StockRestorationRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StockRestorationRepository stockRestorationRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private UUID categoryId;
    private Category existingCategory;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        existingCategory = Category.builder()
                .id(categoryId)
                .name("Sérum")
                .slug("serum")
                .description("Sérums K-beauty")
                .createdAt(LocalDateTime.now())
                .products(new ArrayList<>())
                .build();

        existingProduct = Product.builder()
                .id(productId)
                .category(existingCategory)
                .name("COSRX Snail Essence")
                .brand("COSRX")
                .price(BigDecimal.valueOf(24.99))
                .stock(50)
                .version(0L)
                .skinType(SkinType.ALL)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────
    // searchProducts
    // ─────────────────────────────────────────

    @Test
    @DisplayName("searchProducts — sans filtre, retourne les produits actifs")
    void searchProducts_noFilter_returnsActiveProducts() {
        when(productRepository.search(null, null, null, null, null, null))
                .thenReturn(List.of(existingProduct));

        List<ProductResponse> result =
                productService.searchProducts(null, null, null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("COSRX Snail Essence");
    }

    @Test
    @DisplayName("searchProducts — transmet tous les critères combinés au repository")
    void searchProducts_combinedFilters_passesAllCriteria() {
        BigDecimal min = BigDecimal.valueOf(10);
        BigDecimal max = BigDecimal.valueOf(50);

        when(productRepository.search(categoryId, "COSRX", SkinType.ALL, min, max, true))
                .thenReturn(List.of(existingProduct));

        List<ProductResponse> result = productService.searchProducts(
                categoryId, "COSRX", SkinType.ALL, min, max, true);

        assertThat(result).hasSize(1);

        // Aucun critère n'est perdu en route — l'ancienne chaîne de if
        // n'en appliquait qu'un seul et ignorait les autres en silence
        verify(productRepository).search(categoryId, "COSRX", SkinType.ALL, min, max, true);
    }

    // ─────────────────────────────────────────
    // getProductById
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getProductById — retourne le produit si trouvé")
    void getProductById_found_returnsProduct() {
        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(existingProduct));

        ProductResponse result = productService.getProductById(productId);

        assertThat(result.name()).isEqualTo("COSRX Snail Essence");
        assertThat(result.brand()).isEqualTo("COSRX");
    }

    @Test
    @DisplayName("getProductById — lève ProductNotFoundException si absent")
    void getProductById_notFound_throwsException() {
        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ─────────────────────────────────────────
    // createProduct
    // ─────────────────────────────────────────

    @Test
    @DisplayName("createProduct — crée un produit avec la bonne catégorie")
    void createProduct_validRequest_createsProduct() {
        CreateProductRequest request = new CreateProductRequest(
                categoryId,
                "Laneige Cream",
                "Crème hydratante",
                "Laneige",
                BigDecimal.valueOf(35.00),
                100,
                null,
                SkinType.DRY,
                50
        );

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));
        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct — lève CategoryNotFoundException si catégorie absente")
    void createProduct_unknownCategory_throwsException() {
        CreateProductRequest request = new CreateProductRequest(
                categoryId, "Test", null, "Brand",
                BigDecimal.valueOf(10.00), 10, null, SkinType.ALL, null
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    // ─────────────────────────────────────────
    // decrementStock
    // ─────────────────────────────────────────

    @Test
    @DisplayName("decrementStock — décrémente le stock correctement")
    void decrementStock_sufficientStock_decrementsCorrectly() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        productService.decrementStock(productId, 10);

        assertThat(existingProduct.getStock()).isEqualTo(40);
    }

    @Test
    @DisplayName("decrementStock — lève exception si stock insuffisant")
    void decrementStock_insufficientStock_throwsException() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        assertThatThrownBy(() -> productService.decrementStock(productId, 100))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    // ─────────────────────────────────────────
    // restoreStock
    // ─────────────────────────────────────────

    @Test
    @DisplayName("restoreStock — restaure le stock correctement")
    void restoreStock_validQuantity_restoresCorrectly() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        productService.restoreStock(productId, 10);

        assertThat(existingProduct.getStock()).isEqualTo(60);
    }

    // ─────────────────────────────────────────
    // onPaymentFailed — compensation Saga
    // ─────────────────────────────────────────

    @Test
    @DisplayName("onPaymentFailed — restitue le stock de chaque item et publie stock.restored")
    void onPaymentFailed_restoresStockAndPublishesEvent() {
        UUID orderId = UUID.randomUUID();
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(orderId)
                .userId(UUID.randomUUID())
                .failureReason("Payment declined")
                .items(List.of(OrderItemEvent.builder()
                        .productId(productId)
                        .productName("COSRX Snail Essence")
                        .quantity(3)
                        .unitPrice(BigDecimal.valueOf(24.99))
                        .subtotal(BigDecimal.valueOf(74.97))
                        .build()))
                .build();

        when(stockRestorationRepository.existsById(orderId)).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        productService.onPaymentFailed(event);

        assertThat(existingProduct.getStock()).isEqualTo(53);
        verify(stockRestorationRepository).save(any());
        verify(kafkaTemplate).send(eq("stock.restored"), eq(orderId.toString()), any());
    }

    @Test
    @DisplayName("onPaymentFailed — idempotent, ne restitue pas deux fois le même ordre")
    void onPaymentFailed_isIdempotent_whenAlreadyRestored() {
        UUID orderId = UUID.randomUUID();
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(orderId)
                .items(List.of(OrderItemEvent.builder()
                        .productId(productId)
                        .quantity(3)
                        .build()))
                .build();

        // L'événement a déjà été traité lors d'une livraison précédente
        when(stockRestorationRepository.existsById(orderId)).thenReturn(true);

        productService.onPaymentFailed(event);

        assertThat(existingProduct.getStock()).isEqualTo(50);
        verify(productRepository, never()).save(any(Product.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }
}