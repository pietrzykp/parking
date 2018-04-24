package pl.pietrzyk.parking.domain.rateplan;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class RatePlan {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal firstHourRate;

    @Column(nullable = false)
    private BigDecimal secondHourRate;

    @Column(nullable = false)
    private BigDecimal afterThirdHourMultiplier;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;


    public enum Currency {
        PLN
    }
}
