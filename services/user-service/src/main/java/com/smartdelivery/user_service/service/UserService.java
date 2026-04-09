package com.smartdelivery.user_service.service;

import com.smartdelivery.user_service.dto.AddressResponse;
import com.smartdelivery.user_service.dto.CreateAddressRequest;
import com.smartdelivery.user_service.dto.UpdateUserRequest;
import com.smartdelivery.user_service.dto.UserResponse;
import com.smartdelivery.user_service.entity.Address;
import com.smartdelivery.user_service.entity.User;
import com.smartdelivery.user_service.repository.AddressRepository;
import com.smartdelivery.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public UserResponse getOrCreateUser(Jwt jwt) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());

        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toUserResponse)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .keycloakId(keycloakId)
                            .email(jwt.getClaimAsString("email"))
                            .firstName(jwt.getClaimAsString("given_name"))
                            .lastName(jwt.getClaimAsString("family_name"))
                            .build();
                    return toUserResponse(userRepository.save(newUser));
                });
    }

    // Profil
    @Transactional(readOnly = true)
    public UserResponse getUserByKeycloakId(UUID keycloakId) {
        User user = findUserByKeycloakId(keycloakId);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID keycloakId, UpdateUserRequest request) {
        User user = findUserByKeycloakId(keycloakId);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        return toUserResponse(userRepository.save(user));
    }

    // Address
    @Transactional
    public AddressResponse addAddress(UUID keycloakId, CreateAddressRequest request) {
        User user = findUserByKeycloakId(keycloakId);

        // Si la nouvelle adresse est par défaut, on retire le flag des autres adresses
        if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.findByUserId(user.getId())
                    .forEach(a -> a.setIsDefault(false));
        }

        Address address = Address.builder()
                .user(user)
                .label(request.label())
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country() != null ? request.country() : "FR")
                .isDefault(Boolean.TRUE.equals(request.isDefault()))
                .build();

        return toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse setDefaultAddress(UUID keycloakId, UUID addressId) {
        User user = findUserByKeycloakId(keycloakId);

        addressRepository.findByUserId(user.getId())
                .forEach(a -> a.setIsDefault(false));

        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setIsDefault(true);
        return toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID keycloakId, UUID addressId) {
        User user = findUserByKeycloakId(keycloakId);
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }

    private User findUserByKeycloakId(UUID keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toUserResponse(User user) {
        List<AddressResponse> addresses = addressRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::toAddressResponse)
                .toList();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getActive(),
                user.getCreatedAt(),
                addresses
        );
    }

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault()
        );
    }
}
