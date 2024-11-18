    package org.example.realtime_event_ticketing_system;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class RealTimeEventTicketingSystemApplication  {

        @Autowired
        public static void main(String[] args) {
            SpringApplication.run(RealTimeEventTicketingSystemApplication.class, args);
        }
    }
