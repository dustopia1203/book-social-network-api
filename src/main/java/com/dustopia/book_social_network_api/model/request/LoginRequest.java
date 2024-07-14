package com.dustopia.book_social_network_api.model.request;

public record LoginRequest(
        String email,
        String password
) {
}
