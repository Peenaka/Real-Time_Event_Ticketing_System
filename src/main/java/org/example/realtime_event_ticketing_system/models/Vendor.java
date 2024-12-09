    package org.example.realtime_event_ticketing_system.models;

    import jakarta.persistence.*;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.NoArgsConstructor;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @PrimaryKeyJoinColumn(name = "user_id")
    public class Vendor extends User {
        private String companyName;

        @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
        private List<Ticket> tickets = new ArrayList<>();

        @Override
        protected void onCreate() {
            super.onCreate();
            setRole("ROLE_VENDOR");
        }
    }