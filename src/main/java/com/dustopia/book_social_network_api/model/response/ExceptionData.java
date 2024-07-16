package com.dustopia.book_social_network_api.model.response;

import java.time.LocalDate;

public record ExceptionData(
        LocalDate timestamp,
        String message,
        String detail
) {
}
