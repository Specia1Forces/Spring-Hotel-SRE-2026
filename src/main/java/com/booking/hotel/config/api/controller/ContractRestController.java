package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.ContractCreateRequest;
import com.booking.hotel.config.api.dto.ContractResponse;
import com.booking.hotel.config.api.error.ResourceNotFoundException;
import com.booking.hotel.models.ClientOfHotel;
import com.booking.hotel.models.Contracts;
import com.booking.hotel.service.ClientOfHotelService;
import com.booking.hotel.service.ContractsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractRestController {

    private final ContractsService contractsService;
    private final ClientOfHotelService clientsService;

    public ContractRestController(ContractsService contractsService, ClientOfHotelService clientsService) {
        this.contractsService = contractsService;
        this.clientsService = clientsService;
    }

    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractCreateRequest request) {
        ClientOfHotel client = clientsService.findOne(request.clientId());
        if (client == null) {
            throw new ResourceNotFoundException("Клиент с id=" + request.clientId() + " не найден");
        }

        Contracts contract = new Contracts();
        contract.setClientOfHotel(client);
        contract.setTermOfStay(request.termOfStay());
        contract.setDateConclusionAgreement(request.agreementDate());
        Contracts saved = contractsService.save(contract);

        ContractResponse response = new ContractResponse(
                saved.getContractNumber(),
                client.getClientId(),
                saved.getTermOfStay(),
                saved.getDateConclusionAgreement()
        );
        return ResponseEntity.created(URI.create("/api/v1/contracts/" + saved.getContractNumber())).body(response);
    }
}
