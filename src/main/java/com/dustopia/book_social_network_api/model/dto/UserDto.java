package com.dustopia.book_social_network_api.model.dto;

import java.time.LocalDate;

public record UserDto(
        String fullName,
        LocalDate dateOfBirth,
        String email,
        String role
) {
}
