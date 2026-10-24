package com.booking.hotel.config.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class HotelBusinessMetrics {

    private final Counter successfulLogins;
    private final Counter createdClients;
    private final Counter createdBookings;
    private final Counter createdContracts;

    public HotelBusinessMetrics(MeterRegistry meterRegistry) {
        this.successfulLogins = Counter.builder("hotel.auth.login.success")
                .description("Number of successful user logins")
                .register(meterRegistry);

        this.createdClients = Counter.builder("hotel.clients.created")
                .description("Number of successfully created hotel clients")
                .register(meterRegistry);

        this.createdBookings = Counter.builder("hotel.bookings.created")
                .description("Number of successfully created hotel bookings")
                .register(meterRegistry);

        this.createdContracts = Counter.builder("hotel.contracts.created")
                .description("Number of successfully created hotel contracts")
                .register(meterRegistry);
    }

    public void recordSuccessfulLogin() {
        successfulLogins.increment();
    }

    public void recordCreatedClient() {
        createdClients.increment();
    }

    public void recordCreatedBooking() {
        createdBookings.increment();
    }

    public void recordCreatedContract() {
        createdContracts.increment();
    }
}