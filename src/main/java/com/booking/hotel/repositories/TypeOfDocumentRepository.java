package com.booking.hotel.repositories;


import com.booking.hotel.models.TypeOfDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeOfDocumentRepository extends JpaRepository<TypeOfDocument, Integer> {
}
