
package com.anurag.parking.Dto;

import com.anurag.parking.Model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateBookingRequest(
    @NotBlank String customerName,
    @NotBlank String vehicleNumber,
    @NotNull Long locationId,
    @NotNull Long slotId,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime,
    @NotNull VehicleType vehicleType,
    boolean evRequired,
    boolean accessibleRequired
) {}
