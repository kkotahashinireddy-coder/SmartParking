
package com.anurag.parking.Controller;

import com.anurag.parking.Dto.CreateBookingRequest;
import com.anurag.parking.Model.*;
import com.anurag.parking.Repository.ParkingLocationRepository;
import com.anurag.parking.Service.ParkingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173"})
public class ParkingController {

    private final ParkingService service;
    private final ParkingLocationRepository locations;

    public ParkingController(
            ParkingService service,
            ParkingLocationRepository locations) {
        this.service = service;
        this.locations = locations;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/locations")
    public List<ParkingLocation> getLocations() {
        return service.getLocations();
    }

    @GetMapping("/locations/{id}/slots")
    public List<ParkingSlot> getSlots(@PathVariable Long id) {
        if (!locations.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Location not found");
        }
        return service.getSlots(id);
    }

    @GetMapping("/bookings")
    public List<Booking> getBookings() {
        return service.getBookings();
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        try {
            return service.createBooking(request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/check-in")
    public Booking checkIn(@PathVariable Long id) {
        try {
            return service.checkIn(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/check-out")
    public Booking checkOut(@PathVariable Long id) {
        try {
            return service.checkOut(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/ai/recommend")
    public List<ParkingSlot> recommendSlots(
            @RequestParam Long locationId,
            @RequestParam(defaultValue = "CAR") VehicleType vehicleType,
            @RequestParam(defaultValue = "false") boolean evRequired,
            @RequestParam(defaultValue = "false") boolean accessibleRequired) {
        return service.recommendSlots(
            locationId, vehicleType, evRequired, accessibleRequired);
    }

    @GetMapping("/ai/demand")
    public Map<String, String> demandInsight(
            @RequestParam Long locationId) {
        return Map.of(
            "summary", service.getDemandInsight(locationId));
    }
}
