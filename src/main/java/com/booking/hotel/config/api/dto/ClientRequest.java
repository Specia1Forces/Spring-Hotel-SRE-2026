package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClientRequest(
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String middleName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 50) String gender,
        @NotNull LocalDate birthDate,
        @Size(max = 500) String address,
        @NotBlank @Size(max = 50) String phone
) {
}
