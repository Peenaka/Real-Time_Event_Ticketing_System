package org.example.realtime_event_ticketing_system.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.example.realtime_event_ticketing_system.config.TicketConfig;
import org.example.realtime_event_ticketing_system.dto.TicketConfigRequest;
import org.example.realtime_event_ticketing_system.repositories.TicketConfigRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfigController {
    private final TicketConfigRepository configRepository;

    @PostMapping("/config")
    public ResponseEntity<TicketConfig> configure(@RequestBody TicketConfigRequest request) {
        if (configRepository.existsByIsConfigured(true)) {
            throw new RuntimeException("System is already configured");
        }

        TicketConfig config = TicketConfig.builder()
                .totalTickets(request.getTotalTickets())
                .ticketReleaseRate(request.getTicketReleaseRate())
                .customerRetrievalRate(request.getCustomerRetrievalRate())
                .maxTicketCapacity(request.getMaxTicketCapacity())
                .isConfigured(true)
                .build();

        return ResponseEntity.ok(configRepository.save(config));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        configRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
