package com.booking.hotel.repositories;

import com.booking.hotel.models.CleaningSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleaningScheduleRepository extends JpaRepository<CleaningSchedule, Integer> {
}
