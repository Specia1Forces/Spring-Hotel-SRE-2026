package com.booking.hotel.repositories;

import com.booking.hotel.models.AllServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllServicesRepository extends JpaRepository<AllServices, Integer> {
}
