package com.dustopia.book_social_network_api.model.response;

import java.util.List;

public record PageData<T> (
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
}
