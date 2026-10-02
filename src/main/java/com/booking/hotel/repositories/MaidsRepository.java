package com.booking.hotel.repositories;


import com.booking.hotel.models.Maids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaidsRepository extends JpaRepository<Maids, Integer> {
}
