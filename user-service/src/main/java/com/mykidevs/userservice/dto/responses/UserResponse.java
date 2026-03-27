package com.mykidevs.userservice.dto.responses;


import jakarta.validation.constraints.NotNull;

public record UserResponse(
        @NotNull
        Long id,
        String email,
        String description,
        boolean hasVerifiedEmail
) {
}
