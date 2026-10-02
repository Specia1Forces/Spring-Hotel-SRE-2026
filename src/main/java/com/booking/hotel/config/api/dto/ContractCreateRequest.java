package com.booking.hotel.config.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ContractCreateRequest(
        @NotNull @Positive Integer clientId,
        @NotNull @Positive Integer termOfStay,
        @NotNull LocalDate agreementDate
) {
}
