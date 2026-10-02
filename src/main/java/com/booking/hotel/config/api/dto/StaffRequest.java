package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StaffRequest(
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String middleName,
        @NotBlank @Size(max = 255) String lastName
) {
}
