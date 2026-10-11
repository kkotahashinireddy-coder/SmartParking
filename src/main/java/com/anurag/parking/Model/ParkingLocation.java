
package com.anurag.parking.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parking_location")
public class ParkingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "total_slots", nullable = false)
    private int totalSlots;

    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate = 50.0;

    // Default constructor required by JPA
    public ParkingLocation() {
    }

    // Constructor without ID
    public ParkingLocation(String name, String address, int totalSlots) {
        this.name = name;
        this.address = address;
        this.totalSlots = totalSlots;
        this.hourlyRate = 50.0;
    }

    // Constructor with hourly rate
    public ParkingLocation(
            String name,
            String address,
            int totalSlots,
            double hourlyRate) {
        this.name = name;
        this.address = address;
        this.totalSlots = totalSlots;
        this.hourlyRate = hourlyRate;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
