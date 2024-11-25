package org.example.realtime_event_ticketing_system.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.realtime_event_ticketing_system.models.Event;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_ticket_config")
public class TicketConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private int totalTickets; // Total tickets available for this specific event

    @Column(nullable = false)
    private int ticketReleaseRate; // Number of tickets released per batch

    @Column(nullable = false)
    private int customerRetrievalRate; // Number of customers that can retrieve tickets simultaneously

    @Column(nullable = false)
    private int maxTicketCapacity; // Maximum tickets that can be held in the pool

    @Column(nullable = false)
    private int availableTickets; // Current number of available tickets

    @Column(nullable = false)
    private int soldTickets; // Number of tickets sold

    private boolean isConfigured;

    @Version
    private Long version; // For optimistic locking

    @PrePersist
    protected void onCreate() {
        this.availableTickets = this.totalTickets;
        this.soldTickets = 0;
    }
}