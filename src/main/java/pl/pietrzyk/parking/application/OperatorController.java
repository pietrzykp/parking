package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pietrzyk.parking.application.dto.SlotState;
import pl.pietrzyk.parking.application.exception.ParkingMeterNotFoundException;

@RestController
@RequestMapping("/operator")
@RequiredArgsConstructor
class OperatorController {

    private final OperatorFacade operatorFacade;

    @GetMapping(path = "/is-slot-paid-for", params = "parkingMeterId")
    SlotState isSlotPaidFor(@RequestParam("parkingMeterId") Long parkingMeterId) {
        return operatorFacade.isSlotPaidFor(parkingMeterId);
    }

    @ExceptionHandler(ParkingMeterNotFoundException.class)
    ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
