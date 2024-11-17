package org.example.realtime_event_ticketing_system.dto;


import lombok.Data;

@Data
public class CustomerRegistrationDto {
    private String name;
    private String email;
    private String password;
    private boolean isVIP;
}
