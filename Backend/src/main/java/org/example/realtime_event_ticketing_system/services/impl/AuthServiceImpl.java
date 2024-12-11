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
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.time.LocalDateTime;

    /*** Provides implementation for the AuthService interface, handling authentication-related operations such as customer and vendor registration, login, and password encoding.*/

    @Service
    public class AuthServiceImpl implements AuthService {
        private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
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
                logger.warn("Email {} is already registered", dto.getEmail());
                throw new RuntimeException("Email already registered");
            }

            Customer customer = new Customer();
            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
            customer.setVIP(dto.isVIP());
            customer.setActive(true);

            logger.info("Customer {} registered successfully", customer.getEmail());
            return customerRepository.save(customer);
        }

        @Override
        public Vendor registerVendor(VendorRegistrationDto dto) {
            if (vendorRepository.existsByEmail(dto.getEmail())) {
                logger.warn("Email {} is already registered", dto.getEmail());
                throw new RuntimeException("Email already registered");

            }

            Vendor vendor = new Vendor();
            vendor.setName(dto.getName());
            vendor.setEmail(dto.getEmail());
            vendor.setPassword(passwordEncoder.encode(dto.getPassword()));
            vendor.setCompanyName(dto.getCompanyName());
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setActive(true);

            logger.info("Vendor {} registered successfully", vendor.getEmail());
            return vendorRepository.save(vendor);
        }

        @Override
        public Customer loginCustomer(LoginDto dto) {
            Customer customer = customerRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
                logger.warn("Invalid credentials {}", dto.getPassword());
                throw new RuntimeException("Invalid credentials");
            }

            return customer;
        }

        @Override
        public Vendor loginVendor(LoginDto dto) {
            if (isConfigVendor(dto.getEmail(), dto.getPassword())) {
                logger.warn("Config vendor cannot log in through regular vendor login");
                throw new AuthenticationException("Config vendor cannot log in through regular vendor login");
            }
            Vendor vendor = vendorRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!passwordEncoder.matches(dto.getPassword(), vendor.getPassword())) {
                logger.warn("Invalid credentials {}", dto.getPassword());
                throw new RuntimeException("Invalid credentials");
            }
            return vendor;
        }
        @Override
        public boolean isConfigVendor(String email, String password) {
            // Check if the provided email and password match the config vendor credentials
            return email.equals(configVendorEmail) && password.equals(configVendorPassword);
        }
    }
