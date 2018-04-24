package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pietrzyk.parking.application.dto.ParkingMeterForm;
import pl.pietrzyk.parking.application.dto.PaymentAmount;
import pl.pietrzyk.parking.application.exception.DriverNotFoundException;
import pl.pietrzyk.parking.application.exception.ParkingMeterNotFoundException;
import pl.pietrzyk.parking.application.exception.ParkingSlotStateException;
import pl.pietrzyk.parking.domain.history.PaymentSummary;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
class DriverController {

    private final DriverFacade driverFacade;

    @PostMapping(path = "/meter/start")
    void startParkingMeter(@RequestBody @Valid ParkingMeterForm parkingMeterForm) {
        driverFacade.startParkingMeter(parkingMeterForm);
    }

    @PostMapping(path = "/meter/stop")
    PaymentAmount stopParkingMeter(@RequestBody @Valid ParkingMeterForm parkingMeterForm) {
        return driverFacade.stopParkingMeter(parkingMeterForm);
    }

    @GetMapping(path = "/summary", params = "driverId")
    List<PaymentSummary> getHistorySummary(@RequestParam("driverId") Long driverId) {
        return driverFacade.getHistorySummary(driverId);
    }

    @ExceptionHandler({ParkingMeterNotFoundException.class, DriverNotFoundException.class,
            ParkingSlotStateException.class})
    ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}


