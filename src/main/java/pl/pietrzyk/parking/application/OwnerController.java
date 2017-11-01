package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pietrzyk.parking.application.dto.PaymentAmount;

import java.time.LocalDate;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
class OwnerController {

    private final OwnerFacade ownerFacade;

    @PostMapping(path = "/day-summary")
    PaymentAmount daySummary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ownerFacade.getDayPaymentsSummary(date);
    }

    @ExceptionHandler
    ResponseEntity<String> handleItemNotFoundException(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
