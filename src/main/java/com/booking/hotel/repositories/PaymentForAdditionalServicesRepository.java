package com.booking.hotel.repositories;


import com.booking.hotel.models.PaymentForAdditionalServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentForAdditionalServicesRepository extends JpaRepository<PaymentForAdditionalServices, Integer> {
}
