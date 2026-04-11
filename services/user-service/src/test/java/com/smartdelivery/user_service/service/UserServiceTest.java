package com.smartdelivery.user_service.service;

import com.smartdelivery.user_service.dto.CreateAddressRequest;
import com.smartdelivery.user_service.dto.UpdateUserRequest;
import com.smartdelivery.user_service.dto.UserResponse;
import com.smartdelivery.user_service.entity.Address;
import com.smartdelivery.user_service.entity.User;
import com.smartdelivery.user_service.exception.AddressNotFoundException;
import com.smartdelivery.user_service.exception.UserNotFoundException;
import com.smartdelivery.user_service.repository.AddressRepository;
import com.smartdelivery.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private UserService userService;

    private UUID keycloakId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        keycloakId = UUID.randomUUID();
        existingUser = User.builder()
                .id(UUID.randomUUID())
                .keycloakId(keycloakId)
                .email("laurent@test.com")
                .firstName("Laurent")
                .lastName("Test")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // getOrCreateUser
    @Test
    @DisplayName("getOrCreateUser — retourne l'utilisateur existant si déjà en base")
    void getOrCreateUser_existingUser_returnsExisting() {
        Jwt jwt = mockJwt(keycloakId);
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(existingUser));
        when(addressRepository.findByUserId(existingUser.getId())).thenReturn(List.of());

        UserResponse response = userService.getOrCreateUser(jwt);

        assertThat(response.email()).isEqualTo("laurent@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreateUser — crée un nouvel utilisateur si absent")
    void getOrCreateUser_newUser_createsAndReturns() {
        Jwt jwt = mockJwt(keycloakId);
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(addressRepository.findByUserId(any())).thenReturn(List.of());

        UserResponse response = userService.getOrCreateUser(jwt);

        assertThat(response.email()).isEqualTo("laurent@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // updateUser
    @Test
    @DisplayName("updateUser — met à jour prénom, nom, téléphone")
    void updateUser_validRequest_updatesFields() {
        UpdateUserRequest request = new UpdateUserRequest("Jean", "Dupont", "0600000000");
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(addressRepository.findByUserId(any())).thenReturn(List.of());

        userService.updateUser(keycloakId, request);

        verify(userRepository, times(1)).save(existingUser);
        assertThat(existingUser.getFirstName()).isEqualTo("Jean");
        assertThat(existingUser.getLastName()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("updateUser — lève UserNotFoundException si user absent")
    void updateUser_unknownUser_throwsUserNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest("Jean", "Dupont", null);
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(keycloakId, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    // addAddress
    @Test
    @DisplayName("addAddress — retire le flag isDefault des autres adresses si nouvelle adresse par défaut")
    void addAddress_withDefault_clearsOtherDefaults() {
        Address existingDefault = Address.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .label("Maison")
                .street("1 rue Test")
                .city("Bordeaux")
                .postalCode("33000")
                .country("FR")
                .isDefault(true)
                .build();

        CreateAddressRequest request = new CreateAddressRequest(
                "Bureau", "2 rue Neuve", "Paris", "75001", "FR", true
        );

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(existingUser));
        when(addressRepository.findByUserId(existingUser.getId())).thenReturn(List.of(existingDefault));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        userService.addAddress(keycloakId, request);

        assertThat(existingDefault.getIsDefault()).isFalse();
    }

    // deleteAddress
    @Test
    @DisplayName("deleteAddress — lève AddressNotFoundException si adresse appartient à un autre user")
    void deleteAddress_wrongOwner_throwsAddressNotFoundException() {
        UUID addressId = UUID.randomUUID();
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        Address address = Address.builder()
                .id(addressId)
                .user(otherUser)
                .build();

        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(existingUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        assertThatThrownBy(() -> userService.deleteAddress(keycloakId, addressId))
                .isInstanceOf(AddressNotFoundException.class);
    }

    private Jwt mockJwt(UUID keycloakId) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(keycloakId.toString());
        when(jwt.getClaimAsString("email")).thenReturn("laurent@test.com");
        when(jwt.getClaimAsString("given_name")).thenReturn("Laurent");
        when(jwt.getClaimAsString("family_name")).thenReturn("Test");
        return jwt;
    }
}