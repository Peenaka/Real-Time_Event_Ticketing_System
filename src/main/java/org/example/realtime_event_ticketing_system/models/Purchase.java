    package org.example.realtime_event_ticketing_system.models;

    import jakarta.persistence.*;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Entity
    @Data
    @NoArgsConstructor
    public class Purchase {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private Customer customer;

        @ManyToOne
        @JoinColumn(name = "ticket_id")
        private Ticket ticket;

        private LocalDateTime purchaseTime;
        private String transactionId;

        @PrePersist
        protected void onCreate() {
            purchaseTime = LocalDateTime.now();
            transactionId = generateTransactionId();
        }

        private String generateTransactionId() {
            return "TXN" + System.currentTimeMillis();
        }
    }
