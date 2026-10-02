package com.booking.hotel.config.api.dto;

import java.time.LocalDate;

public record ClientResponse(
        int id,
        String firstName,
        String middleName,
        String lastName,
        String gender,
        LocalDate birthDate,
        String address,
        String phone
) {
}
