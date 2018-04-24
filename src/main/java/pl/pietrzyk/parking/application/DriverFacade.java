package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.pietrzyk.parking.application.dto.ParkingMeterForm;
import pl.pietrzyk.parking.application.dto.PaymentAmount;
import pl.pietrzyk.parking.application.exception.DriverNotFoundException;
import pl.pietrzyk.parking.application.exception.ParkingMeterNotFoundException;
import pl.pietrzyk.parking.application.exception.ParkingSlotStateException;
import pl.pietrzyk.parking.domain.driver.Driver;
import pl.pietrzyk.parking.domain.driver.DriverRepository;
import pl.pietrzyk.parking.domain.history.History;
import pl.pietrzyk.parking.domain.history.HistoryRepository;
import pl.pietrzyk.parking.domain.history.PaymentSummary;
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeter;
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeterRepository;
import pl.pietrzyk.parking.domain.rateplan.RatePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverFacade {

    private final DriverRepository driverRepository;
    private final HistoryRepository historyRepository;
    private final ParkingMeterRepository parkingMeterRepository;

    private final static int MIN_STARTED_HOURS = 1;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void startParkingMeter(ParkingMeterForm parkingMeterForm) {
        Driver driver = driverRepository.findById(parkingMeterForm.getDriverId()).orElseThrow(DriverNotFoundException::new);

        ParkingMeter parkingMeter = parkingMeterRepository.findById(parkingMeterForm.getParkingMeterId())
                .orElseThrow(ParkingMeterNotFoundException::new);

        if(parkingMeter.isTaken()) {
            throw ParkingSlotStateException.slotTakenException();
        }

        parkingMeter.setDriver(driver);
        parkingMeter.setStartDateTime(LocalDateTime.now());
        parkingMeter.setTaken(true);

        parkingMeterRepository.save(parkingMeter);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public PaymentAmount stopParkingMeter(ParkingMeterForm parkingMeterForm) {
        Driver driver = driverRepository.findById(parkingMeterForm.getDriverId()).orElseThrow(DriverNotFoundException::new);

        ParkingMeter parkingMeter = parkingMeterRepository.findById(parkingMeterForm.getParkingMeterId())
                .orElseThrow(ParkingMeterNotFoundException::new);

        if(!parkingMeter.isTaken()) {
            throw ParkingSlotStateException.slotEmptyException();
        }

        if(!driver.equals(parkingMeter.getDriver())) {
            throw ParkingSlotStateException.driverNotMatchingSlotException();
        }

        LocalDateTime parkingEndTime = LocalDateTime.now();

        BigDecimal payment = calculatePayment(parkingMeter.getStartDateTime(), parkingEndTime, driver.getRatePlan());

        historyRepository.save(History.builder()
                .currency(driver.getRatePlan().getCurrency())
                .driver(driver)
                .startDateTime(parkingMeter.getStartDateTime())
                .endDateTime(parkingEndTime)
                .paymentAmount(payment).build());

        parkingMeter.setEndDateTime(parkingEndTime);
        parkingMeterRepository.save(parkingMeter);

        return new PaymentAmount(payment);
    }

    public List<PaymentSummary> getHistorySummary(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(DriverNotFoundException::new);
        return historyRepository.getDriverPaymentSummary(driver);
    }

    BigDecimal calculatePayment(LocalDateTime start, LocalDateTime end, RatePlan ratePlan) {
        long startedHours = Duration.between(start, end).toHours() + 1;

        if(startedHours < MIN_STARTED_HOURS)
            return new BigDecimal(0);

        BigDecimal paymentAmount = ratePlan.getFirstHourRate();
        BigDecimal stepsAmount = new BigDecimal(startedHours - 1);

        if(stepsAmount.intValue() > 0) {
            BigDecimal paymentAfterFirsthour = countSumOfGeometricSeries(ratePlan.getSecondHourRate(),
                    ratePlan.getAfterThirdHourMultiplier(), stepsAmount);
            paymentAmount = paymentAmount.add(paymentAfterFirsthour);
        }

        return paymentAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Counts sum of geometric series, according to formula:
     *
     * a(1-r^n) / (1-r), where a = base, r = common ratio, n = number of steps
     */
    private BigDecimal countSumOfGeometricSeries(BigDecimal base, BigDecimal commonRatio, BigDecimal numberOfSteps) {
        BigDecimal one = BigDecimal.ONE;
        BigDecimal numerator = base.multiply(one.subtract(commonRatio.pow(numberOfSteps.intValue())));
        BigDecimal denominator = BigDecimal.ONE.subtract(commonRatio);

        return numerator.divide(denominator);
    }
}
