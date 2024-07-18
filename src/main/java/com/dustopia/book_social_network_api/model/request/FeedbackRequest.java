package com.dustopia.book_social_network_api.model.request;

import jakarta.validation.constraints.*;

public record FeedbackRequest(
        @Positive
        @Max(5)
        @Min(0)
        Double star,
        @NotBlank
        String comment,
        @NotNull
        Long bookId
) {
}
