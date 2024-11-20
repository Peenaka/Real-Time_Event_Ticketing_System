package org.example.realtime_event_ticketing_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private String eventName;
    private float price;
    private LocalDateTime eventDateTime;
    private String venue;
    private boolean isVIP;
}
