package org.example.config;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SystemConfig {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
    private int vendorCount;
    private int customerCount;

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        if (maxTicketCapacity > totalTickets) {
            throw new IllegalArgumentException("Max Ticket Capacity must be less than Total Tickets.");
        }
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getVendorCount() {
        return vendorCount;
    }

    public void setVendorCount(int vendorCount) {
        this.vendorCount = vendorCount;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public static SystemConfig loadConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return new Gson().fromJson(reader, SystemConfig.class);
        } catch (IOException e) {
            System.out.println("No existing configuration found. Using defaults.");
            return new SystemConfig(); // Return a default config if file not found
        }
    }

    public void saveConfig(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            new Gson().toJson(this, writer);
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }
}
