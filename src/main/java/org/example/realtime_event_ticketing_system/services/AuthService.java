package org.example.realtime_event_ticketing_system.services;

import jakarta.transaction.Transactional;
import org.example.realtime_event_ticketing_system.dto.CustomerRegistrationDto;
import org.example.realtime_event_ticketing_system.dto.LoginDto;
import org.example.realtime_event_ticketing_system.dto.VendorRegistrationDto;
import org.example.realtime_event_ticketing_system.models.Customer;
import org.example.realtime_event_ticketing_system.models.User;
//import org.example.realtime_event_ticketing_system.models.Vendor;
import org.example.realtime_event_ticketing_system.repositories.CustomerRepository;
import org.example.realtime_event_ticketing_system.repositories.UserRepository;
//import org.example.realtime_event_ticketing_system.repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

//    @Autowired
//    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public Customer registerCustomer(CustomerRegistrationDto dto) {
//        if (customerRepository.existsByEmail(dto.getEmail())) {
//            throw new RuntimeException("Email already registered");
//        }

        Customer customer = new Customer();

        // Validate required fields
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        User user = userRepository.save(User.builder().name(dto.getName()).email(dto.getEmail()).password(dto.getPassword()).role("ROLE_CUSTOMER").build());
        if (user != null) {
            customer.setVIP(true);
            customer.setUser(user);
            return customerRepository.save(customer);

        }
        else {
            throw new RuntimeException("User not found");
        }

    }


//    public Vendor registerVendor(VendorRegistrationDto dto) {
//        if (vendorRepository.existsByEmail(dto.getEmail())) {
//            throw new RuntimeException("Email already registered");
//        }
//
//        Vendor vendor = new Vendor();
//        vendor.setName(dto.getName());
//        vendor.setEmail(dto.getEmail());
//        vendor.setPassword(passwordEncoder.encode(dto.getPassword()));
//        vendor.setCompanyName(dto.getCompanyName());
//
//        return vendorRepository.save(vendor);
//    }

//    public Customer loginCustomer(LoginDto dto) {
//        Customer customer = customerRepository.findByEmail(dto.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(dto.getPassword(), customer.getUser().getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        return customer;
//    }

//    public Vendor loginVendor(LoginDto dto) {
//        Vendor vendor = vendorRepository.findByEmail(dto.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(dto.getPassword(), vendor.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        return vendor;
//    }
}
