
package com.anurag.parking.Service;

import com.anurag.parking.Dto.CreateBookingRequest;
import com.anurag.parking.Model.*;
import com.anurag.parking.Repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {

    private final ParkingLocationRepository locations;
    private final ParkingSlotRepository slots;
    private final BookingRepository bookings;

    public ParkingService(
            ParkingLocationRepository locations,
            ParkingSlotRepository slots,
            BookingRepository bookings) {
        this.locations = locations;
        this.slots = slots;
        this.bookings = bookings;
    }

    public List<ParkingLocation> getLocations() {
        return locations.findAll();
    }

    public List<ParkingSlot> getSlots(Long locationId) {
        return slots.findByLocationId(locationId);
    }

    public List<Booking> getBookings() {
        return bookings.findAllByOrderByIdDesc();
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request) {

        if (request.startTime() == null ||
                request.endTime() == null) {
            throw new IllegalArgumentException(
                    "Start time and end time are required.");
        }

        if (request.customerName() == null ||
                request.customerName().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name is required.");
        }

        if (request.vehicleNumber() == null ||
                request.vehicleNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle number is required.");
        }

        if (request.locationId() == null ||
                request.slotId() == null) {
            throw new IllegalArgumentException(
                    "Location and parking slot are required.");
        }

        if (request.vehicleType() == null) {
            throw new IllegalArgumentException(
                    "Vehicle type is required.");
        }

        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time.");
        }

        if (request.startTime().isBefore(
                LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException(
                    "Start time must be now or in the future.");
        }

        ParkingLocation location = locations
                .findById(request.locationId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Parking location not found."));

        ParkingSlot slot = slots.findById(request.slotId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Parking slot not found."));

        if (slot.getLocation() == null ||
                !slot.getLocation().getId().equals(location.getId())) {
            throw new IllegalArgumentException(
                    "Selected slot does not belong to this location.");
        }

        if (slot.isOccupied()) {
            throw new IllegalArgumentException(
                    "Parking slot is currently occupied.");
        }

        if (slot.getVehicleType() != request.vehicleType()) {
            throw new IllegalArgumentException(
                    "Vehicle type does not match the selected slot.");
        }

        if (request.evRequired() && !slot.isEvCharging()) {
            throw new IllegalArgumentException(
                    "An EV charging slot is required.");
        }

        if (request.accessibleRequired() && !slot.isAccessible()) {
            throw new IllegalArgumentException(
                    "An accessible parking slot is required.");
        }

        boolean overlaps = bookings
                .findByLocationId(location.getId())
                .stream()
                .filter(b -> b.getSlot() != null)
                .filter(b -> b.getSlot().getId().equals(slot.getId()))
                .filter(b ->
                        b.getStatus() == BookingStatus.BOOKED ||
                        b.getStatus() == BookingStatus.CHECKED_IN)
                .anyMatch(b ->
                        request.startTime().isBefore(b.getEndTime()) &&
                        request.endTime().isAfter(b.getStartTime()));

        if (overlaps) {
            throw new IllegalArgumentException(
                    "This slot is already booked during that time.");
        }

        long minutes = Duration.between(
                request.startTime(), request.endTime()).toMinutes();

        long hours = Math.max(1, (minutes + 59) / 60);

        Booking booking = new Booking();
        booking.setCustomerName(request.customerName().trim());
        booking.setVehicleNumber(
                request.vehicleNumber().trim().toUpperCase());
        booking.setLocation(location);
        booking.setSlot(slot);
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());
        booking.setEstimatedFee(hours * location.getHourlyRate());
        booking.setStatus(BookingStatus.BOOKED);

        return bookings.save(booking);
    }

    @Transactional
    public Booking checkIn(Long id) {

        Booking booking = bookings.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + id));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new IllegalArgumentException(
                    "Only a BOOKED reservation can be checked in.");
        }

        if (booking.getSlot() == null) {
            throw new IllegalArgumentException(
                    "No parking slot is assigned to this booking.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(booking.getStartTime())) {
            throw new IllegalArgumentException(
                    "Check-in is not available yet. Booking starts at "
                            + booking.getStartTime()
                            + ". Please check in at or after that time.");
        }

        if (now.isAfter(booking.getEndTime())) {
            throw new IllegalArgumentException(
                    "This booking has expired. Please create a new booking.");
        }

        if (booking.getSlot().isOccupied()) {
            throw new IllegalArgumentException(
                    "This parking slot is already occupied.");
        }

        booking.setActualCheckIn(now);
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.getSlot().setOccupied(true);

        slots.save(booking.getSlot());
        return bookings.save(booking);
    }

    @Transactional
    public Booking checkOut(Long id) {

        Booking booking = bookings.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + id));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalArgumentException(
                    "Only a CHECKED_IN booking can be checked out.");
        }

        if (booking.getSlot() == null) {
            throw new IllegalArgumentException(
                    "No parking slot is assigned to this booking.");
        }

        LocalDateTime now = LocalDateTime.now();

        booking.setActualCheckOut(now);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.getSlot().setOccupied(false);

        slots.save(booking.getSlot());
        return bookings.save(booking);
    }

    public List<ParkingSlot> recommendSlots(
            Long locationId,
            VehicleType vehicleType,
            boolean evRequired,
            boolean accessibleRequired) {

        return slots.findByLocationId(locationId).stream()
                .filter(slot -> !slot.isOccupied())
                .filter(slot -> slot.getVehicleType() == vehicleType)
                .filter(slot -> !evRequired || slot.isEvCharging())
                .filter(slot ->
                        !accessibleRequired || slot.isAccessible())
                .toList();
    }

    public String getDemandInsight(Long locationId) {

        List<ParkingSlot> allSlots =
                slots.findByLocationId(locationId);

        if (allSlots.isEmpty()) {
            return "No parking slots available for this location.";
        }

        long occupied = allSlots.stream()
                .filter(ParkingSlot::isOccupied)
                .count();

        int percentage = (int) Math.round(
                occupied * 100.0 / allSlots.size());

        if (percentage >= 80) {
            return "High current occupancy: " + percentage + "%";
        }

        if (percentage >= 50) {
            return "Moderate current occupancy: " + percentage + "%";
        }

        return "Low current occupancy: " + percentage + "%";
    }
}