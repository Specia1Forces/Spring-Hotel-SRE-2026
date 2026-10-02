package com.booking.hotel.config.api.dto;

import java.time.LocalDate;

public record ContractResponse(int id, int clientId, int termOfStay, LocalDate agreementDate) {
}
