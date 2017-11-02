package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pietrzyk.parking.domain.history.HistoryRepository;
import pl.pietrzyk.parking.domain.history.PaymentSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
class OwnerFacade {

    private final HistoryRepository historyRepository;

    List<PaymentSummary> getDayPaymentsSummary(LocalDate paymentDate) {
        LocalDateTime start = LocalDateTime.of
                (paymentDate.getYear(), paymentDate.getMonth(), paymentDate.getDayOfMonth(), 0, 0);
        LocalDateTime end = start.plus(1, ChronoUnit.DAYS);
        return historyRepository.getIntervalPaymentsSummary(start, end);
    }

}
