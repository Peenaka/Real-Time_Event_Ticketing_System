package org.example.realtime_event_ticketing_system.controllers;



import org.example.realtime_event_ticketing_system.dto.ApiResponse;
import org.example.realtime_event_ticketing_system.dto.CustomerRegistrationDto;
import org.example.realtime_event_ticketing_system.dto.LoginDto;
import org.example.realtime_event_ticketing_system.dto.VendorRegistrationDto;
import org.example.realtime_event_ticketing_system.models.Customer;
import org.example.realtime_event_ticketing_system.models.Vendor;
import org.example.realtime_event_ticketing_system.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/customer/register")
    public ResponseEntity<ApiResponse<?>> registerCustomer(@RequestBody CustomerRegistrationDto dto) {
        try {
            Customer customer = authService.registerCustomer(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", customer.getId());
            response.put("name", customer.getName());
            response.put("email", customer.getEmail());
            response.put("isVIP", customer.isVIP());
            return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/vendor/register")
    public ResponseEntity<ApiResponse<?>> registerVendor(@RequestBody VendorRegistrationDto dto) {
        try {
            Vendor vendor = authService.registerVendor(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", vendor.getId());
            response.put("name", vendor.getName());
            response.put("email", vendor.getEmail());
            response.put("companyName", vendor.getCompanyName());
            return ResponseEntity.ok(ApiResponse.success("Vendor registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/customer/login")
    public ResponseEntity<ApiResponse<?>> loginCustomer(@RequestBody LoginDto dto) {
        try {
            Customer customer = authService.loginCustomer(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", customer.getId());
            response.put("name", customer.getName());
            response.put("email", customer.getEmail());
            response.put("isVIP", customer.isVIP());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/vendor/login")
    public ResponseEntity<ApiResponse<?>> loginVendor(@RequestBody LoginDto dto) {
        try {
            Vendor vendor = authService.loginVendor(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("id", vendor.getId());
            response.put("name", vendor.getName());
            response.put("email", vendor.getEmail());
            response.put("companyName", vendor.getCompanyName());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}