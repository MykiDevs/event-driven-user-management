package com.mykidevs.userservice.dto.requests;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPagePaginationRequest(
        @Min(value = 0, message = "Page index must be less than zero")
        @Parameter(name = "Page index", example = "0")
        Integer page,

        @Min(value = 1, message = "Page size must be more than 1")
        @Max(value = 100, message = "Page size must be less than 100")
        @Parameter(name = "Page size", example = "10")
        Integer size,

        @Parameter(name = "Sort by", example = "id")
        String sortBy,

        @Pattern(regexp = "ASC|DESC", message = "Sort direction must be ASC or DESC")
        @Parameter(name = "Sort direction", example = "ASC")
        String direction
) {
    public UserPagePaginationRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null || sortBy.isBlank()) sortBy = "id";
        if (direction == null || sortBy.isBlank()) direction = "ASC";
    }
}
