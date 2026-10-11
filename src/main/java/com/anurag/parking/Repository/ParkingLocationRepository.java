
package com.anurag.parking.Repository;

import com.anurag.parking.Model.ParkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLocationRepository
        extends JpaRepository<ParkingLocation, Long> {

}
