package com.dustopia.book_social_network_api.model.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        String synopsis,
        String bookCoverUrl,
        String bookUrl,
        String user,
        Double rate,
        boolean isShareable
) {
}
