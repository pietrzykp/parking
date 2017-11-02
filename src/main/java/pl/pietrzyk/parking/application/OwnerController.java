package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pietrzyk.parking.domain.history.PaymentSummary;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
class OwnerController {

    private final OwnerFacade ownerFacade;

    @GetMapping(path = "/day-summary")
    List<PaymentSummary> daySummary(@RequestParam("date")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ownerFacade.getDayPaymentsSummary(date);
    }

    @ExceptionHandler
    ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
