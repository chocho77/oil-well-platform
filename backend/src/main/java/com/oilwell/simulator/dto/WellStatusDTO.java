package com.oilwell.simulator.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WellStatusDTO {
    private Double flowRate;
    private Boolean valveOpen;
    private String alertStatus;
    private LocalDateTime measurementTime;
    private LocalDateTime highFlowStartTime;
    private String displayMessage;
}
