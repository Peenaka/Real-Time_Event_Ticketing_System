    package org.example.realtime_event_ticketing_system;

    import io.swagger.v3.oas.annotations.OpenAPIDefinition;
    import io.swagger.v3.oas.annotations.info.Info;
    import io.swagger.v3.oas.annotations.servers.Server;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    @SpringBootApplication
    public class RealTimeEventTicketingSystemApplication {

        public static void main(String[] args) {
            SpringApplication.run(RealTimeEventTicketingSystemApplication.class, args);
        }
    }