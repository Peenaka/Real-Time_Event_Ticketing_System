package org.example.realtime_event_ticketing_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    @Schema(description = "Name of the event", example = "Summer Music Festival 2024")
    @NotBlank(message = "Event name is required")
    private String eventName;

    @Schema(description = "Ticket price")
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @Schema(description = "Number of tickets to create")
    @NotNull(message = "Ticket count is required")
    @Min(value = 1, message = "Ticket count must be at least 1")
    private Integer ticketCount;

    @Schema(description = "Whether the ticket is VIP")
    private boolean isVIP;
}
