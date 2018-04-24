package pl.pietrzyk.parking.domain.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.pietrzyk.parking.domain.rateplan.RatePlan;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentSummary {
    private BigDecimal sum;
    private RatePlan.Currency currency;
}
