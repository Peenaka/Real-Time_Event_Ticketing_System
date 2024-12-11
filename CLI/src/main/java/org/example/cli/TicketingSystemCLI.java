    package org.example.cli;

    import org.example.config.SystemConfig;
    import org.example.ticket.TicketConsumer;
    import org.example.ticket.TicketPool;
    import org.example.ticket.TicketProducer;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;
    import java.util.Scanner;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.concurrent.Future;

    public class TicketingSystemCLI {
        private final SystemConfig config;
        private ExecutorService executorService;
        private final List<Future<?>> ticketOperations;
        private boolean isRunning;
        private TicketPool ticketPool;
        private static final String CONFIG_FILE = "config.json";

        public TicketingSystemCLI() {
            this.config = SystemConfig.loadConfig(CONFIG_FILE);
            this.ticketOperations = new ArrayList<>();
            this.isRunning = false;
        }

        public void setupConfiguration() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Real-Time Ticketing System Configuration");

            int totalTickets = promptForInt(scanner, "Enter total tickets (must be a positive number): ", 1, Integer.MAX_VALUE);
            config.setTotalTickets(totalTickets);

            int ticketReleaseRate = promptForInt(scanner, "Enter ticket release rate (must be a positive number): ", 1, Integer.MAX_VALUE);
            config.setTicketReleaseRate(ticketReleaseRate);

            int customerRetrievalRate = promptForInt(scanner, "Enter customer retrieval rate (must be a positive number): ", 1, Integer.MAX_VALUE);
            config.setCustomerRetrievalRate(customerRetrievalRate);

            int maxTicketCapacity = promptForInt(scanner, "Enter max ticket capacity (must be less than total tickets): ", 1, totalTickets );
            config.setMaxTicketCapacity(maxTicketCapacity);

            int vendorCount = promptForInt(scanner, "Enter vendor count (must be a positive number): ", 1, Integer.MAX_VALUE);
            config.setVendorCount(vendorCount);

            int customerCount = promptForInt(scanner, "Enter customer count (must be a positive number): ", 1, Integer.MAX_VALUE);
            config.setCustomerCount(customerCount);

            System.out.println("Configuration set up successfully!");
            config.saveConfig(CONFIG_FILE);
            ticketPool = new TicketPool(config.getMaxTicketCapacity());
        }

        private int promptForInt(Scanner scanner, String message, int min, int max) {
            int value;
            while (true) {
                System.out.print(message);
                if (scanner.hasNextInt()) {
                    value = scanner.nextInt();
                    if (value >= min && value <= max) {
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.next();
                }
            }
            return value;
        }

        public void start() {

            if (isRunning) {
                System.out.println("Ticketing system is already running.");
                return;
            }

            System.out.println("Starting ticketing operations...");
            isRunning = true;
            executorService = Executors.newFixedThreadPool(config.getVendorCount() + config.getCustomerCount());

            // Start vendor threads
            for (int i = 0; i < config.getVendorCount(); i++) {
                TicketProducer producer = new TicketProducer(ticketPool, config);
                ticketOperations.add(executorService.submit(producer));
            }

            // Start customer threads
            for (int i = 0; i < config.getCustomerCount(); i++) {
                TicketConsumer consumer = new TicketConsumer(ticketPool, config);
                ticketOperations.add(executorService.submit(consumer));
            }

            // Start monitoring thread
            startMonitoring();

            System.out.println("Ticketing operations started successfully.");
        }


        public void stop() {
            if (!isRunning) {
                System.out.println("Ticketing system is not running.");
                return;
            }

            System.out.println("Stopping ticketing operations...");
            isRunning = false;

            for (Future<?> operation : ticketOperations) {
                operation.cancel(true); // Interrupt each thread
            }

            if (executorService != null) {
                executorService.shutdownNow();
            }
            ticketOperations.clear();
            System.out.println("Ticketing operations stopped.");
        }

        private void startMonitoring() {
            Thread monitorThread = new Thread(() -> {
                while (isRunning) {
                    try {
                        int ticketCount = ticketPool.getTicketCount();
                        synchronized (config) {
                            int remainingTickets = config.getTotalTickets();

                            System.out.println("Real-Time Update: Total Tickets in Pool = " + ticketCount + ", Remaining Tickets = " + remainingTickets);

                            // Stop the system if all tickets are sold and the pool is empty
                            if (remainingTickets <= 0 && ticketCount == 0) {
                                System.out.println("All tickets are sold. Shutting down the system...");
                                stop();
                                break;
                            }
                        }
                        Thread.sleep(2000); // Check every 2 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Monitoring interrupted.");
                        break;
                    }
                }
            });
            monitorThread.setDaemon(true);
            monitorThread.start();
        }

        public static void main(String[] args) {
            TicketingSystemCLI cli = new TicketingSystemCLI();
            cli.setupConfiguration();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter command (start/stop/exit): ");
                String command = scanner.nextLine();

                if ("start".equalsIgnoreCase(command)) {
                    cli.start();
                } else if ("stop".equalsIgnoreCase(command)) {
                    cli.stop();
                } else if ("exit".equalsIgnoreCase(command)) {
                    cli.stop();
                    break;
                } else {
                    System.out.println("Invalid command. Please enter 'start', 'stop', or 'exit'.");
                }
            }
            scanner.close();
        }
    }
