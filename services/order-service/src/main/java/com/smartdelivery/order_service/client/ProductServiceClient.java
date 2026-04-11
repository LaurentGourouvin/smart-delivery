package com.smartdelivery.order_service.client;

import com.smartdelivery.order_service.exception.InsufficientStockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient restClient;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    public ProductResponse getProduct(UUID productId, String bearerToken) {
        return restClient.get()
                .uri(productServiceUrl + "/api/products/" + productId)
                .header("Authorization", bearerToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Product not found: " + productId);
                })
                .body(ProductResponse.class);
    }

    public void decrementStock(UUID productId, Integer quantity, String bearerToken) {
        restClient.patch()
                .uri(productServiceUrl + "/api/products/" + productId
                        + "/stock/decrement?quantity=" + quantity)
                .header("Authorization", bearerToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new InsufficientStockException(productId);
                })
                .toBodilessEntity();
    }

    public void restoreStock(UUID productId, Integer quantity, String bearerToken) {
        restClient.patch()
                .uri(productServiceUrl + "/api/products/" + productId
                        + "/stock/restore?quantity=" + quantity)
                .header("Authorization", bearerToken)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("Failed to restore stock for product {}", productId);
                })
                .toBodilessEntity();
    }
}