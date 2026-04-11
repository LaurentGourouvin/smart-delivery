package com.smartdelivery.product_service.service;

import com.smartdelivery.product_service.dto.CreateProductRequest;
import com.smartdelivery.product_service.dto.ProductResponse;
import com.smartdelivery.product_service.entity.Category;
import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import com.smartdelivery.product_service.exception.CategoryNotFoundException;
import com.smartdelivery.product_service.exception.ProductNotFoundException;
import com.smartdelivery.product_service.repository.CategoryRepository;
import com.smartdelivery.product_service.repository.ProductRepository;
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
    // getAllProducts
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getAllProducts — retourne les produits actifs")
    void getAllProducts_returnsActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(existingProduct));

        List<ProductResponse> result = productService.getAllProducts();


        System.out.println("Result size: " + result.size());
        System.out.println("Product active: " + existingProduct.getActive());
        System.out.println("Product id: " + existingProduct.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("COSRX Snail Essence");
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
}