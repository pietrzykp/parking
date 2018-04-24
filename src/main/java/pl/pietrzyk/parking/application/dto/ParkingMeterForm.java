package pl.pietrzyk.parking.application.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParkingMeterForm {
    @NotNull
    private Long driverId;
    @NotNull
    private Long parkingMeterId;
}

