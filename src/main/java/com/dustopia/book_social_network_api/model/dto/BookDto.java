package com.dustopia.book_social_network_api.model.dto;

public record BookDto(
        String title,
        String author,
        String synopsis,
        String bookCover,
        String user,
        Double rate
) {
}
