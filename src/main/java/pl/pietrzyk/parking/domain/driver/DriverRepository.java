package pl.pietrzyk.parking.domain.driver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by paulina on 28.10.2017.
 */
public interface DriverRepository extends JpaRepository<Driver, String> {

    Optional<Driver> findById(Long id);
}
