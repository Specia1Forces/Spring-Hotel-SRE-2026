package com.booking.hotel.config.api.dto;

import java.util.List;

public record AuthUserResponse(String username, List<String> roles) {
}
