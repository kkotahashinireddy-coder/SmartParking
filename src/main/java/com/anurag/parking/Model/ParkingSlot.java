
package com.anurag.parking.Model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "parking_slot",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_location_slot_code",
        columnNames = {"location_id", "slot_code"}
    )
)
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_code", nullable = false)
    private String slotCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "ev_charging", nullable = false)
    private boolean evCharging;

    @Column(name = "is_accessible", nullable = false)
    private boolean accessible;

    @Column(name = "occupied", nullable = false)
    private boolean occupied = false;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private ParkingLocation location;

    public ParkingSlot() {
    }

    public ParkingSlot(
            String slotCode,
            VehicleType vehicleType,
            boolean evCharging,
            boolean accessible,
            ParkingLocation location) {
        this.slotCode = slotCode;
        this.vehicleType = vehicleType;
        this.evCharging = evCharging;
        this.accessible = accessible;
        this.location = location;
        this.occupied = false;
    }

    public Long getId() {
        return id;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isEvCharging() {
        return evCharging;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public ParkingLocation getLocation() {
        return location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setEvCharging(boolean evCharging) {
        this.evCharging = evCharging;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void setLocation(ParkingLocation location) {
        this.location = location;
    }
}
