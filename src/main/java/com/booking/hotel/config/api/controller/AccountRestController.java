package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.AccountCreateRequest;
import com.booking.hotel.config.api.dto.AccountResponse;
import com.booking.hotel.config.api.error.ResourceNotFoundException;
import com.booking.hotel.models.Accounts;
import com.booking.hotel.models.Booking;
import com.booking.hotel.service.AccountsService;
import com.booking.hotel.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountRestController {

    private final AccountsService accountsService;
    private final BookingService bookingService;

    public AccountRestController(AccountsService accountsService, BookingService bookingService) {
        this.accountsService = accountsService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountsService.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest request) {
        Booking booking = bookingService.findOne(request.bookingId());
        if (booking == null) {
            throw new ResourceNotFoundException("Бронирование с id=" + request.bookingId() + " не найдено");
        }

        Accounts account = new Accounts();
        account.setBooking(booking);
        account.setAmountToBePaid(request.amountDue());
        account.setAmountOfPrepayment(request.prepaymentAmount());
        account.setAmountForAdditionalServices(request.additionalServicesAmount());

        Accounts saved = accountsService.save(account);
        return ResponseEntity.created(URI.create("/api/v1/accounts/" + saved.getAccountNumber()))
                .body(toResponse(saved));
    }

    private AccountResponse toResponse(Accounts account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getBooking().getReservationId(),
                account.getAmountToBePaid(),
                account.getAmountOfPrepayment(),
                account.getAmountForAdditionalServices()
        );
    }
}
