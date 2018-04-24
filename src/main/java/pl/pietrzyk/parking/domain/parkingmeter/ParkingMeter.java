package pl.pietrzyk.parking.domain.parkingmeter;


import lombok.Data;
import pl.pietrzyk.parking.domain.driver.Driver;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ParkingMeter {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    @Column(nullable = false)
    private boolean taken;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;
}
