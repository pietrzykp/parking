package pl.pietrzyk.parking.domain.parkingmeter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingMeterRepository extends JpaRepository<ParkingMeter, String> {

    Optional<ParkingMeter> findById(Long id);
}