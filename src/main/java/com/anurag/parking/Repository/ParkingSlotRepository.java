
package com.anurag.parking.Repository;

import com.anurag.parking.Model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParkingSlotRepository
        extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByLocationId(Long locationId);
}
