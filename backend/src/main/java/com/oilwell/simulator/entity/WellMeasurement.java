package com.oilwell.simulator.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "well_measurements")
@Data
public class WellMeasurement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flow_rate")
    private Double flowRate;
    
    @Column(name = "valve_status")
    private Boolean valveOpen;
    
    @Column(name = "alert_status")
    private String alertStatus; // "NORMAL", "HIGH_FLOW"
    
    @Column(name = "measurement_time")
    private LocalDateTime measurementTime;
    
    @Column(name = "high_flow_start_time")
    private LocalDateTime highFlowStartTime;
}