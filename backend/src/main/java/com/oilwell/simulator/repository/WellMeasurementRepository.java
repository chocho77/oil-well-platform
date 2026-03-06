package com.oilwell.simulator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oilwell.simulator.entity.WellMeasurement;

@Repository
public interface WellMeasurementRepository extends JpaRepository<WellMeasurement, Long> {
    
    @Query("SELECT w FROM WellMeasurement w ORDER BY w.measurementTime DESC LIMIT 1")
    Optional<WellMeasurement> findLatestMeasurement();
}
