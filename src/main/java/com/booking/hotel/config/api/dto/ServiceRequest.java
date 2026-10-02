package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ServiceRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull @PositiveOrZero Integer price
) {
}
