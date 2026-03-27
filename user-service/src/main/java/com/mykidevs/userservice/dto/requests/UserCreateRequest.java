package com.mykidevs.userservice.dto.requests;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public record UserCreateRequest(

        @Email(message = "Invalid email!")
        @NotEmpty(message = "Email can't be empty!")
        @Size(min = 6, max = 30, message = "Email must be between 6 and 30 characters!")
        @Schema(description = "User's work email", example = "user@example.com")
        String email,

        @NotEmpty(message = "Invalid password!")
        @Size(min = 8, max = 25, message = "Password must be between 8 and 25 characters!")
        @Schema(description = "Account password", example = "password")
        String password,
        @Size(min = 10, max = 100, message = "Description must be between 10 and 100 characters!")
        @NotEmpty(message = "Invalid desc!")
        @Schema(description = "Short user bio", example = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed...")
        String description) {
}
