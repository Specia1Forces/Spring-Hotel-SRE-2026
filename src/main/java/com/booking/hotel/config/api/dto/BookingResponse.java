package com.booking.hotel.config.api.dto;

import java.time.LocalDate;

public record BookingResponse(
        int id,
        int clientId,
        String clientName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int totalAmount,
        String prepaymentStatus,
        String bookingStatus,
        String residenceStatus
) {
}
