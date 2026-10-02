package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.ClientRequest;
import com.booking.hotel.config.api.dto.ClientResponse;
import com.booking.hotel.models.ClientOfHotel;
import com.booking.hotel.service.ClientOfHotelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientRestController {

    private final ClientOfHotelService service;

    public ClientRestController(ClientOfHotelService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClientResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        ClientOfHotel client = new ClientOfHotel();
        client.setName(request.firstName());
        client.setMiddleName(request.middleName());
        client.setLastName(request.lastName());
        client.setGender(request.gender());
        client.setBirthdate(request.birthDate());
        client.setAddress(request.address());
        client.setPhone(request.phone());

        ClientOfHotel saved = service.save(client);
        return ResponseEntity.created(URI.create("/api/v1/clients/" + saved.getClientId()))
                .body(toResponse(saved));
    }

    private ClientResponse toResponse(ClientOfHotel client) {
        return new ClientResponse(
                client.getClientId(),
                client.getName(),
                client.getMiddleName(),
                client.getLastName(),
                client.getGender(),
                client.getBirthdate(),
                client.getAddress(),
                client.getPhone()
        );
    }
}
