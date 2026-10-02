package com.booking.hotel.config.api.dto;

public record CsrfResponse(String headerName, String parameterName, String token) {
}
