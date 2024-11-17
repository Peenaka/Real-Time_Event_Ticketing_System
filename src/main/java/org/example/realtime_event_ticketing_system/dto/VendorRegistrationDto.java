package org.example.realtime_event_ticketing_system.dto;

import lombok.Data;

@Data
public class VendorRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String companyName;
}
