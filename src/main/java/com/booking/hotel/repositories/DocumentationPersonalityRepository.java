package com.booking.hotel.repositories;


import com.booking.hotel.models.DocumentationPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentationPersonalityRepository extends JpaRepository<DocumentationPersonality, Integer> {
}
