package com.dustopia.book_social_network_api.model.dto;

public record FeedbackDto(
        Double star,
        String comment,
        boolean isOwner
) {
}
