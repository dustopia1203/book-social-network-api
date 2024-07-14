package com.dustopia.book_social_network_api.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull(message = "Firstname is mandatory")
        @NotBlank(message = "Firstname is mandatory")
        String firstName,
        @NotNull(message = "Lastname is mandatory")
        @NotBlank(message = "Lastname is mandatory")
        String lastName,
        @Email(message = "Wrong email format")
        @NotNull(message = "Email is mandatory")
        @NotBlank(message = "Email is mandatory")
        String email,
        @NotNull(message = "Password is mandatory")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password should be at least 6 character")
        String password
) {
}
