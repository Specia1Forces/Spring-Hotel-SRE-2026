package com.booking.hotel.repositories;


import com.booking.hotel.models.RelBetweenRoomsBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelBetweenRoomsBookingRepository extends JpaRepository<RelBetweenRoomsBooking, Integer> {
}
