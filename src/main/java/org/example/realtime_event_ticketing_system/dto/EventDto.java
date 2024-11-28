package org.example.realtime_event_ticketing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventDto {
    @Schema(description = "Name of the event", example = "Summer Music Festival 2024")
    @NotBlank(message = "Event name is required")
    private String eventName;
}