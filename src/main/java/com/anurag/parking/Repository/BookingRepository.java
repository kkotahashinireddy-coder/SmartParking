
package com.anurag.parking.Repository;

import com.anurag.parking.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findAllByOrderByIdDesc();

    List<Booking> findByLocationId(Long locationId);
}
