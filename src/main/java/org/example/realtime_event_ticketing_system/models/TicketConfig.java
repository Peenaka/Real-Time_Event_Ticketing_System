package org.example.realtime_event_ticketing_system.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.realtime_event_ticketing_system.dto.TicketConfigDto;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private Integer totalTickets;
    private Integer ticketReleaseRate;
    private Integer customerRetrievalRate;
    private Integer maxTicketCapacity;
    private boolean isConfigured;

    @JsonIgnore
    public TicketConfigDto toDto() {
        return TicketConfigDto.builder()
                .totalTickets(totalTickets)
                .ticketReleaseRate(ticketReleaseRate)
                .customerRetrievalRate(customerRetrievalRate)
                .maxTicketCapacity(maxTicketCapacity)
                .build();
    }
}