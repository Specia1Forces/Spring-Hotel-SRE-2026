package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.BookingCreateRequest;
import com.booking.hotel.config.api.dto.BookingResponse;
import com.booking.hotel.config.api.error.BadRequestException;
import com.booking.hotel.config.api.error.ResourceNotFoundException;
import com.booking.hotel.models.Booking;
import com.booking.hotel.models.ClientOfHotel;
import com.booking.hotel.service.BookingService;
import com.booking.hotel.service.ClientOfHotelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingRestController {

    private final BookingService bookingService;
    private final ClientOfHotelService clientService;

    public BookingRestController(BookingService bookingService, ClientOfHotelService clientService) {
        this.bookingService = bookingService;
        this.clientService = clientService;
    }

    @GetMapping
    public List<BookingResponse> findAll() {
        return bookingService.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingCreateRequest request) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new BadRequestException("Дата выезда должна быть позже даты заезда");
        }

        ClientOfHotel client = clientService.findOne(request.clientId());
        if (client == null) {
            throw new ResourceNotFoundException("Клиент с id=" + request.clientId() + " не найден");
        }

        Booking booking = new Booking();
        booking.setClientOfHotel1(client);
        booking.setSettlementDate(request.checkInDate());
        booking.setEvictionDate(request.checkOutDate());
        booking.setTotalAmountOfPayment(request.totalAmount());
        booking.setPrepaymentStatus(request.prepaymentStatus());
        booking.setBookingStatus(request.bookingStatus());
        booking.setResidenceStatus(request.residenceStatus());

        Booking saved = bookingService.save(booking);
        return ResponseEntity.created(URI.create("/api/v1/bookings/" + saved.getReservationId()))
                .body(toResponse(saved));
    }

    private BookingResponse toResponse(Booking booking) {
        ClientOfHotel client = booking.getClientOfHotel1();
        String clientName = Stream.of(client.getLastName(), client.getName(), client.getMiddleName())
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse("");
        return new BookingResponse(
                booking.getReservationId(),
                client.getClientId(),
                clientName,
                booking.getSettlementDate(),
                booking.getEvictionDate(),
                booking.getTotalAmountOfPayment(),
                booking.getPrepaymentStatus(),
                booking.getBookingStatus(),
                booking.getResidenceStatus()
        );
    }
}
