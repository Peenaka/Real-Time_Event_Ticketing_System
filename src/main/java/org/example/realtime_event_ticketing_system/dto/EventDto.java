package org.example.realtime_event_ticketing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.realtime_event_ticketing_system.models.EventStatus;

@Data
public class EventDto {
    @Schema(description = "Name of the event", example = "Summer Music Festival 2024")
    @NotBlank(message = "Event name is required")
    private String eventName;

    @Schema(description = "Event code", example = "EVT-2024-001")
    private String eventCode;

    @Schema(description = "Event status")
    @NotNull(message = "Event status is required")
    private EventStatus status;
}
