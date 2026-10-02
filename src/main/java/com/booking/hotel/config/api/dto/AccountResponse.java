package com.booking.hotel.config.api.dto;

public record AccountResponse(
        int id,
        int bookingId,
        int amountDue,
        int prepaymentAmount,
        int additionalServicesAmount
) {
}
