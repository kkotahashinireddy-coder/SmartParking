
package com.anurag.parking.Config;

import com.anurag.parking.Model.*;
import com.anurag.parking.Repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoData {

    @Bean
    CommandLineRunner loadDemoData(
            ParkingLocationRepository locations,
            ParkingSlotRepository slots) {

        return args -> {
            if (locations.count() > 0) {
                return;
            }

            ParkingLocation mall = locations.save(
                new ParkingLocation(
                    "City Center Mall",
                    "Madhapur, Hyderabad", 50));

            ParkingLocation hospital = locations.save(
                new ParkingLocation(
                    "Care Hospital Parking",
                    "Banjara Hills, Hyderabad", 40));

            slots.save(new ParkingSlot(
                "A-01", VehicleType.CAR, false, false, mall));
            slots.save(new ParkingSlot(
                "A-02", VehicleType.CAR, true, false, mall));
            slots.save(new ParkingSlot(
                "A-03", VehicleType.CAR, false, true, mall));
            slots.save(new ParkingSlot(
                "B-01", VehicleType.SUV, false, false, mall));
            slots.save(new ParkingSlot(
                "B-02", VehicleType.BIKE, false, false, mall));

            slots.save(new ParkingSlot(
                "H-01", VehicleType.CAR, false, true, hospital));
            slots.save(new ParkingSlot(
                "H-02", VehicleType.CAR, true, false, hospital));
            slots.save(new ParkingSlot(
                "H-03", VehicleType.SUV, false, false, hospital));
            slots.save(new ParkingSlot(
                "H-04", VehicleType.BIKE, false, false, hospital));
        };
    }
}
