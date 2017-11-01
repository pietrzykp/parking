package pl.pietrzyk.parking.domain.driver;

import lombok.Data;
import pl.pietrzyk.parking.domain.rateplan.RatePlan;

import javax.persistence.*;

@Entity
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(nullable=false)
    private RatePlan ratePlan;

}
