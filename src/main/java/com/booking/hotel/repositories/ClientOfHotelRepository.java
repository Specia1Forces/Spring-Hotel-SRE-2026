package com.booking.hotel.repositories;


import com.booking.hotel.models.ClientOfHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientOfHotelRepository extends JpaRepository<ClientOfHotel, Integer> {
}
