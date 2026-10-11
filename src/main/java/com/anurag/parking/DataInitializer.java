
package com.anurag.parking;

import com.anurag.parking.Model.ParkingLocation;
import com.anurag.parking.Model.ParkingSlot;
import com.anurag.parking.Model.VehicleType;
import com.anurag.parking.Repository.ParkingLocationRepository;
import com.anurag.parking.Repository.ParkingSlotRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;

    public DataInitializer(
            ParkingLocationRepository locationRepository,
            ParkingSlotRepository slotRepository) {
        this.locationRepository = locationRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(String... args) {
        createSlots(1L);
        createSlots(2L);
    }

    @Transactional
    private void createSlots(Long locationId) {

        ParkingLocation location = locationRepository
                .findById(locationId)
                .orElse(null);

        if (location == null) {
            System.out.println("Location not found: " + locationId);
            return;
        }

        // Check how many slots already exist for this location.
        int existingSlots =
                slotRepository.findByLocationId(locationId).size();

        if (existingSlots > 0) {

            // Update the location's total slot count.
            location.setTotalSlots(existingSlots);
            locationRepository.save(location);

            System.out.println(
                    "Slots already exist for location: " + locationId
                    + ". Total slots: " + existingSlots);

            return;
        }

        // Create six parking slots.
        slotRepository.save(new ParkingSlot(
                "CAR-01", VehicleType.CAR, false, false, location));

        slotRepository.save(new ParkingSlot(
                "CAR-02", VehicleType.CAR, false, false, location));

        slotRepository.save(new ParkingSlot(
                "CAR-03", VehicleType.CAR, true, false, location));

        slotRepository.save(new ParkingSlot(
                "BIKE-01", VehicleType.BIKE, false, false, location));

        slotRepository.save(new ParkingSlot(
                "SUV-01", VehicleType.SUV, false, false, location));

        slotRepository.save(new ParkingSlot(
                "EV-01", VehicleType.EV, true, false, location));

        // Update and save the location's total slot count.
        location.setTotalSlots(6);
        locationRepository.save(location);

        System.out.println(
                "Created 6 parking slots for location: " + locationId);
    }
}
