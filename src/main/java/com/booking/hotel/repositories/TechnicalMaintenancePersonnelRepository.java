package com.booking.hotel.repositories;


import com.booking.hotel.models.TechnicalMaintenancePersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalMaintenancePersonnelRepository extends JpaRepository<TechnicalMaintenancePersonnel, Integer> {
}
