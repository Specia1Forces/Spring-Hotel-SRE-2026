package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record BookingCreateRequest(
        @NotNull @Positive Integer clientId,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate,
        @NotNull @PositiveOrZero Integer totalAmount,
        @NotBlank String prepaymentStatus,
        @NotBlank String bookingStatus,
        @NotBlank String residenceStatus
) {
}
