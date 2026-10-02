package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AccountCreateRequest(
        @NotNull @Positive Integer bookingId,
        @NotNull @PositiveOrZero Integer amountDue,
        @NotNull @PositiveOrZero Integer prepaymentAmount,
        @NotNull @PositiveOrZero Integer additionalServicesAmount
) {
}
