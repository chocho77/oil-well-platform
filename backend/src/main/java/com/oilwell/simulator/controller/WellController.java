package com.oilwell.simulator.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oilwell.simulator.entity.WellMeasurement;
import com.oilwell.simulator.repository.WellMeasurementRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/well")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class WellController {
    
    private final WellMeasurementRepository measurementRepository;
    
    @GetMapping("/status")
    public WellMeasurement getLatestStatus() {
        return measurementRepository.findLatestMeasurement()
                .orElseGet(() -> {
                    WellMeasurement empty = new WellMeasurement();
                    empty.setFlowRate(0.0);
                    empty.setValveOpen(true);
                    empty.setAlertStatus("NORMAL");
                    empty.setMeasurementTime(LocalDateTime.now());
                    return empty;
                });
    }
    
    @GetMapping("/history")
    public List<WellMeasurement> getHistory() {
        return measurementRepository.findAll();
    }
}
