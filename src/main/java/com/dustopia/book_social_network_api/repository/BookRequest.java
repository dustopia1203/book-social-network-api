package com.dustopia.book_social_network_api.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
    @NotBlank
    String title,
    @NotBlank
    String author,
    String synopsis,
    @NotNull
    boolean isShareable
) {
}
