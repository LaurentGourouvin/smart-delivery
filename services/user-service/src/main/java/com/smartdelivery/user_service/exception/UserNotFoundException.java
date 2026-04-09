package com.smartdelivery.user_service.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID keycloakId) {
        super("User not found with keycloakId: " + keycloakId);
    }
}