package com.booking.hotel.config.api.controller;


import com.booking.hotel.config.api.dto.StaffRequest;
import com.booking.hotel.config.api.dto.StaffResponse;
import com.booking.hotel.models.Maids;
import com.booking.hotel.models.TechnicalMaintenancePersonnel;
import com.booking.hotel.service.MaidsService;
import com.booking.hotel.service.TechnicalMaintenancePersonnelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffRestController {

    private final MaidsService maidsService;
    private final TechnicalMaintenancePersonnelService maintenanceService;

    public StaffRestController(MaidsService maidsService, TechnicalMaintenancePersonnelService maintenanceService) {
        this.maidsService = maidsService;
        this.maintenanceService = maintenanceService;
    }

    @GetMapping("/maids")
    public List<StaffResponse> findMaids() {
        return maidsService.findAll().stream()
                .map(maid -> new StaffResponse(maid.getMaidId(), maid.getName(), maid.getMiddleName(), maid.getLastName()))
                .toList();
    }

    @PostMapping("/maids")
    public ResponseEntity<StaffResponse> createMaid(@Valid @RequestBody StaffRequest request) {
        Maids maid = new Maids();
        maid.setName(request.firstName());
        maid.setMiddleName(request.middleName());
        maid.setLastName(request.lastName());
        Maids saved = maidsService.save(maid);
        StaffResponse response = new StaffResponse(
                saved.getMaidId(), saved.getName(), saved.getMiddleName(), saved.getLastName()
        );
        return ResponseEntity.created(URI.create("/api/v1/staff/maids/" + saved.getMaidId())).body(response);
    }

    @GetMapping("/maintenance")
    public List<StaffResponse> findMaintenanceStaff() {
        return maintenanceService.findAll().stream()
                .map(employee -> new StaffResponse(
                        employee.getCollaboratorMaintenanceID(),
                        employee.getName(),
                        employee.getMiddleName(),
                        employee.getLastName()
                ))
                .toList();
    }

    @PostMapping("/maintenance")
    public ResponseEntity<StaffResponse> createMaintenanceEmployee(@Valid @RequestBody StaffRequest request) {
        TechnicalMaintenancePersonnel employee = new TechnicalMaintenancePersonnel();
        employee.setName(request.firstName());
        employee.setMiddleName(request.middleName());
        employee.setLastName(request.lastName());
        TechnicalMaintenancePersonnel saved = maintenanceService.save(employee);
        StaffResponse response = new StaffResponse(
                saved.getCollaboratorMaintenanceID(),
                saved.getName(),
                saved.getMiddleName(),
                saved.getLastName()
        );
        return ResponseEntity.created(URI.create(
                "/api/v1/staff/maintenance/" + saved.getCollaboratorMaintenanceID()
        )).body(response);
    }
}
