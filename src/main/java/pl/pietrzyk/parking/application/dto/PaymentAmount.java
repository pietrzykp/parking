package pl.pietrzyk.parking.application.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class PaymentAmount {
    private BigDecimal payment;
}
