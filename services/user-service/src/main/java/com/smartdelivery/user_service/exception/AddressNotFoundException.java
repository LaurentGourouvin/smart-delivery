package com.smartdelivery.user_service.exception;

import java.util.UUID;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(UUID addressId) {
        super("Address not found with id: " + addressId);
    }
}