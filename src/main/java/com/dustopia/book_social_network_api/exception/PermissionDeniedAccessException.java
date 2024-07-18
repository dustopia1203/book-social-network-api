package com.dustopia.book_social_network_api.exception;

public class PermissionDeniedAccessException extends RuntimeException {

    public PermissionDeniedAccessException(String message) {
        super(message);
    }

}
