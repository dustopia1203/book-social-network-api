package com.dustopia.book_social_network_api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(
        @JsonProperty("access_token")
        String token,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
