package pl.pietrzyk.parking.domain.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.pietrzyk.parking.domain.driver.Driver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, String> {

    @Query("select new pl.pietrzyk.parking.domain.history.PaymentSummary(sum(h.paymentAmount) as sum, " +
            "h.currency as currency) " +
            "from History h " +
            "where h.driver = ?1 " +
            "group by h.driver, h.currency")
    List<PaymentSummary> getDriverPaymentSummary(Driver driver);

    @Query("select new pl.pietrzyk.parking.domain.history.PaymentSummary(sum(h.paymentAmount) as sum, " +
            "h.currency as currency) " +
            "from History h " +
            "where h.endDateTime >= :start and h.endDateTime < :end " +
            "group by h.currency")
    List<PaymentSummary> getIntervalPaymentsSummary(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);
}


