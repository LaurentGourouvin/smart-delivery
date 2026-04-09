package com.smartdelivery.user_service.controller;

import com.smartdelivery.user_service.dto.AddressResponse;
import com.smartdelivery.user_service.dto.CreateAddressRequest;
import com.smartdelivery.user_service.dto.UpdateUserRequest;
import com.smartdelivery.user_service.dto.UserResponse;
import com.smartdelivery.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Profil
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getOrCreateUser(jwt));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userService.updateUser(keycloakId, request));
    }

    // Address
    @PostMapping("/me/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(keycloakId, request));
    }

    @PatchMapping("/me/addresses/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID addressId
    ) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userService.setDefaultAddress(keycloakId, addressId));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID addressId
    ) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        userService.deleteAddress(keycloakId, addressId);
        return ResponseEntity.noContent().build();
    }
}