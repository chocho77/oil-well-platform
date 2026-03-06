package com.oilwell.simulator.service;

import com.oilwell.simulator.dto.WellStatusDTO;
import com.oilwell.simulator.entity.WellMeasurement;
import com.oilwell.simulator.repository.WellMeasurementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Random;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class WellSimulatorService {
    
    private final WellMeasurementRepository measurementRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();
    
    private static final double NORMAL_FLOW_MIN = 50.0;
    private static final double NORMAL_FLOW_MAX = 150.0;
    private static final double HIGH_FLOW_THRESHOLD = 200.0;
    private static final double MAX_FLOW = 300.0;
    private static final Duration HIGH_FLOW_DURATION_THRESHOLD = Duration.ofSeconds(10);
    
    private boolean valveOpen = true;
    private LocalDateTime highFlowStartTime = null;
    
    @Scheduled(fixedDelay = 2000) // Всяка 2 секунди
    public void simulateFlow() {
        try {
            // Симулиране на поток
            double flowRate = simulateFlowRate();
            
            // Проверка за висок поток
            String alertStatus = "NORMAL";
            LocalDateTime currentHighFlowStart = highFlowStartTime;
            
            if (flowRate > HIGH_FLOW_THRESHOLD) {
                if (highFlowStartTime == null) {
                    highFlowStartTime = LocalDateTime.now();
                    currentHighFlowStart = highFlowStartTime;
                }
                
                // Проверка дали високия поток продължава повече от 10 секунди
                if (Duration.between(highFlowStartTime, LocalDateTime.now()).compareTo(HIGH_FLOW_DURATION_THRESHOLD) >= 0) {
                    alertStatus = "HIGH_FLOW";
                    // Затваряне на крана при продължителен висок поток
                    if (valveOpen) {
                        valveOpen = false;
                        log.warn("Висок поток! Кранът е затворен!");
                    }
                }
            } else {
                highFlowStartTime = null;
                currentHighFlowStart = null;
                // Отваряне на крана при нормален поток
                if (!valveOpen && flowRate < HIGH_FLOW_THRESHOLD) {
                    valveOpen = true;
                    log.info("Потокът е нормален. Кранът е отворен.");
                }
            }
            
            // Създаване на запис
            WellMeasurement measurement = new WellMeasurement();
            measurement.setFlowRate(flowRate);
            measurement.setValveOpen(valveOpen);
            measurement.setAlertStatus(alertStatus);
            measurement.setMeasurementTime(LocalDateTime.now());
            measurement.setHighFlowStartTime(currentHighFlowStart);
            
            // Запазване в базата данни
            measurementRepository.save(measurement);
            
            // Изпращане на актуално състояние към клиентите
            WellStatusDTO status = new WellStatusDTO();
            status.setFlowRate(flowRate);
            status.setValveOpen(valveOpen);
            status.setAlertStatus(alertStatus);
            status.setMeasurementTime(measurement.getMeasurementTime());
            status.setHighFlowStartTime(currentHighFlowStart);
            
            if (alertStatus.equals("HIGH_FLOW")) {
                status.setDisplayMessage("⚠️ ВИСОК ПОТОК! Крайно допустимата стойност е надвишена!");
            } else {
                status.setDisplayMessage("");
            }
            
            messagingTemplate.convertAndSend("/topic/well-status", status);
            
            log.info("Flow Rate: {:.2f}, Valve: {}, Alert: {}", flowRate, valveOpen ? "OPEN" : "CLOSED", alertStatus);
            
        } catch (Exception e) {
            log.error("Грешка при симулация: {}", e.getMessage());
        }
    }
    
    private double simulateFlowRate() {
        // Понякога симулираме висок поток за тестване
        if (random.nextInt(10) < 2) { // 20% шанс за висок поток
            return NORMAL_FLOW_MAX + random.nextDouble() * (MAX_FLOW - NORMAL_FLOW_MAX);
        } else {
            return NORMAL_FLOW_MIN + random.nextDouble() * (NORMAL_FLOW_MAX - NORMAL_FLOW_MIN);
        }
    }
    
    @Scheduled(fixedDelay = 5000)
    public void checkValveStatus() {
        WellMeasurement latest = measurementRepository.findLatestMeasurement().orElse(null);
        if (latest != null && latest.getAlertStatus().equals("HIGH_FLOW")) {
            log.warn("⚠️ КРИТИЧНО: Високият поток продължава! Кранът е затворен!");
        }
    }
}