package org.example.realtime_event_ticketing_system.utils;

import org.example.realtime_event_ticketing_system.exceptions.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
    private static final String CONFIG_VENDOR_EMAIL = "VendorUser@gmail.com";
    private static final String CONFIG_VENDOR_PASSWORD = "vendor@user123";

    public void validateConfigVendor(String email, String password) {
        if (!CONFIG_VENDOR_EMAIL.equals(email) || !CONFIG_VENDOR_PASSWORD.equals(password)) {
            throw new AuthenticationException("Invalid config vendor credentials");
        }
    }
}
