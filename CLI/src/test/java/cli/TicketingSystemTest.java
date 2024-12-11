package cli;

import org.example.config.SystemConfig;
import org.example.ticket.TicketConsumer;
import org.example.ticket.TicketPool;
import org.example.ticket.TicketProducer;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class TicketingSystemTest {

    @Test()
    void testVendorsAndCustomersOperateAtEqualRates() {
        // Test Case 1: Balanced ticket production and consumption
        SystemConfig config = createConfig(10, 1, 1, 5, 2, 2);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertEquals(0, pool.getTicketCount(), "All tickets should be consumed.");
        });
    }

    @Test
    void testCustomersConsumeFasterThanVendors() {
        // Test Case 2: Faster consumption than production
        SystemConfig config = createConfig(15, 2, 1, 5, 1, 3);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertTrue(pool.getTicketCount() < config.getMaxTicketCapacity(), "Customers should deplete the pool faster than it fills.");
        });
    }

    @Test
    void testVendorsProduceFasterThanCustomersConsume() {
        // Test Case 3: Faster production than consumption
        SystemConfig config = createConfig(20, 1, 3, 10, 2, 2);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertEquals(0, pool.getTicketCount(), "Total tickets now: 0 ");
        });
    }

    @Test
    void testVendorsAndCustomersOperateAtSameRateWithEdgeCases() {
        // Test Case 4: Equal production and consumption rates
        SystemConfig config = createConfig(25, 2, 2, 10, 1, 1);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertEquals(0, pool.getTicketCount(), "Tickets should be produced and consumed at the same rate, leaving the pool empty.");
        });
    }

    @Test
    void testInvalidConfigurationInput() {
        // Test Case 5: Invalid input handling
        SystemConfig config = createConfig(0, 1, 1, 10, 2, 2);
        assertThrows(IllegalArgumentException.class, () -> config.setTotalTickets(0), "Invalid input should throw an exception.");
    }

    @Test
    void testConcurrentOperationsEnsureConsistency() {
        // Test Case 6: Concurrent operations
        SystemConfig config = createConfig(10, 1, 1, 10, 3, 3);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertEquals(0, pool.getTicketCount(), "All tickets should be consumed without race conditions.");
        });
    }

    @Test
    void testHighVolumeStressTest() {
        // Test Case 7: Stress testing
        SystemConfig config = createConfig(100, 1, 2, 20, 1, 4);
        TicketPool pool = new TicketPool(config.getMaxTicketCapacity());

        runTest(config, pool, () -> {
            assertEquals(0, config.getTotalTickets(), "All tickets should be sold.");
        });
    }

    private SystemConfig createConfig(int totalTickets, int releaseRate, int retrievalRate, int maxCapacity, int vendorCount, int customerCount) {
        SystemConfig config = new SystemConfig();
        config.setTotalTickets(totalTickets);
        config.setTicketReleaseRate(releaseRate);
        config.setCustomerRetrievalRate(retrievalRate);
        config.setMaxTicketCapacity(maxCapacity);
        config.setVendorCount(vendorCount);
        config.setCustomerCount(customerCount);
        return config;
    }

    private void runTest(SystemConfig config, TicketPool pool, Runnable assertions) {
        ExecutorService executorService = Executors.newFixedThreadPool(config.getVendorCount() + config.getCustomerCount());
        try {
            // Start producers
            for (int i = 0; i < config.getVendorCount(); i++) {
                executorService.submit(new TicketProducer(pool, config));
            }
            // Start consumers
            for (int i = 0; i < config.getCustomerCount(); i++) {
                executorService.submit(new TicketConsumer(pool, config));
            }

            Thread.sleep(100000); // Allow threads to process

            // Run assertions after operations
            assertions.run();
        } catch (InterruptedException e) {
            fail("Test interrupted unexpectedly.");
        } finally {
            executorService.shutdownNow();
        }
    }
}
