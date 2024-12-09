    package org.example.realtime_event_ticketing_system.services.impl;

    import org.example.realtime_event_ticketing_system.dto.CustomerRegistrationDto;
    import org.example.realtime_event_ticketing_system.dto.LoginDto;
    import org.example.realtime_event_ticketing_system.dto.VendorRegistrationDto;
    import org.example.realtime_event_ticketing_system.exceptions.AuthenticationException;
    import org.example.realtime_event_ticketing_system.models.Customer;
    import org.example.realtime_event_ticketing_system.models.Vendor;
    import org.example.realtime_event_ticketing_system.repositories.CustomerRepository;
    import org.example.realtime_event_ticketing_system.repositories.VendorRepository;
    import org.example.realtime_event_ticketing_system.services.AuthService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;

    @Service
    public class AuthServiceImpl implements AuthService {

        @Autowired
        private CustomerRepository customerRepository;

        @Autowired
        private VendorRepository vendorRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Value("${config.vendor.email}")
        private String configVendorEmail;

        @Value("${config.vendor.password}")
        private String configVendorPassword;

        public AuthServiceImpl(CustomerRepository customerRepository, VendorRepository vendorRepository) {
            this.customerRepository = customerRepository;
            this.vendorRepository = vendorRepository;
        }

        @Override
        public Customer registerCustomer(CustomerRegistrationDto dto) {
            if (customerRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered");
            }

            Customer customer = new Customer();
            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
            customer.setVIP(dto.isVIP());
            customer.setActive(true);


            return customerRepository.save(customer);
        }

        @Override
        public Vendor registerVendor(VendorRegistrationDto dto) {
            if (vendorRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered");
            }

            Vendor vendor = new Vendor();
            vendor.setName(dto.getName());
            vendor.setEmail(dto.getEmail());
            vendor.setPassword(passwordEncoder.encode(dto.getPassword()));
            vendor.setCompanyName(dto.getCompanyName());
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setActive(true);

            return vendorRepository.save(vendor);
        }

        @Override
        public Customer loginCustomer(LoginDto dto) {
            Customer customer = customerRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            return customer;
        }

        @Override
        public Vendor loginVendor(LoginDto dto) {
            if (isConfigVendor(dto.getEmail(), dto.getPassword())) {
                throw new AuthenticationException("Config vendor cannot log in through regular vendor login");
            }
            Vendor vendor = vendorRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(dto.getPassword(), vendor.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            return vendor;
        }
        @Override
        public boolean isConfigVendor(String email, String password) {
            return email.equals(configVendorEmail) && password.equals(configVendorPassword);
        }
    }
