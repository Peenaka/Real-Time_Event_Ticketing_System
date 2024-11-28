package org.example.realtime_event_ticketing_system.services;

public interface JwtService {
    String generateConfigVendorToken(String email);
    boolean validateConfigVendorToken(String token);
    String getEmailFromToken(String token);
}