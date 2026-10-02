package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.ServiceRequest;
import com.booking.hotel.config.api.dto.ServiceResponse;
import com.booking.hotel.config.api.error.ResourceNotFoundException;
import com.booking.hotel.models.AllServices;
import com.booking.hotel.service.AllServicesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    private final AllServicesService service;

    public ServiceRestController(AllServicesService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceResponse> findAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ServiceResponse findOne(@PathVariable int id) {
        return toResponse(requireService(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        AllServices entity = new AllServices();
        apply(entity, request);
        AllServices saved = service.save(entity);
        return ResponseEntity.created(URI.create("/api/v1/services/" + saved.getServiceId()))
                .body(toResponse(saved));
    }

    @PatchMapping("/{id}")
    public ServiceResponse update(@PathVariable int id, @Valid @RequestBody ServiceRequest request) {
        AllServices entity = requireService(id);
        apply(entity, request);
        return toResponse(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        requireService(id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AllServices requireService(int id) {
        AllServices entity = service.findOne(id);
        if (entity == null) {
            throw new ResourceNotFoundException("Услуга с id=" + id + " не найдена");
        }
        return entity;
    }

    private void apply(AllServices entity, ServiceRequest request) {
        entity.setName(request.name());
        entity.setServicePrice(request.price());
    }

    private ServiceResponse toResponse(AllServices entity) {
        return new ServiceResponse(entity.getServiceId(), entity.getName(), entity.getServicePrice());
    }
}
