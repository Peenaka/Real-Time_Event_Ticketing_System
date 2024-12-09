package org.example.realtime_event_ticketing_system.services;

import org.example.realtime_event_ticketing_system.dto.CustomerRegistrationDto;
import org.example.realtime_event_ticketing_system.dto.LoginDto;
import org.example.realtime_event_ticketing_system.dto.VendorRegistrationDto;
import org.example.realtime_event_ticketing_system.models.Customer;
import org.example.realtime_event_ticketing_system.models.Vendor;

public interface AuthService {
    Customer registerCustomer(CustomerRegistrationDto dto);

    Vendor registerVendor(VendorRegistrationDto dto);

    Customer loginCustomer(LoginDto dto);

    Vendor loginVendor(LoginDto dto);
    boolean isConfigVendor(String email, String password);

}
