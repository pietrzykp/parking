package pl.pietrzyk.parking.application

import pl.pietrzyk.parking.domain.driver.DriverRepository
import pl.pietrzyk.parking.domain.history.HistoryRepository
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeterRepository
import pl.pietrzyk.parking.domain.rateplan.RatePlan
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

class AmountCalculationSpec extends Specification  {

    private DriverFacade driverFacade

    def setup() {
        driverFacade = new DriverFacade(Stub(DriverRepository.class), Stub(HistoryRepository.class),
                Stub(ParkingMeterRepository.class))
    }

    @Unroll
    def "test1"(LocalDateTime start, LocalDateTime end, RatePlan ratePlan, BigDecimal result) {
        expect:
        driverFacade.calculatePayment(start, end, ratePlan) == result

        where:

        start | end | ratePlan | result
        LocalDateTime.of(2001, 1, 1, 1, 0) | LocalDateTime.of(2001, 1, 1, 1, 9) | getRatePlan(1, 2, 2)| BigDecimal.ONE
        LocalDateTime.of(2001, 1, 1, 1, 0) | LocalDateTime.of(2001, 1, 1, 5, 0) | getRatePlan(4, 1, 1.5)| BigDecimal.valueOf(12.13)
        LocalDateTime.of(2001, 1, 1, 1, 0) | LocalDateTime.of(2001, 1, 1, 6, 0) | getRatePlan(0, 2, 1.5)| BigDecimal.valueOf(26.38)
    }

    def getRatePlan(double firstHour, double secondHour, double multiplier) {
        return new RatePlan(description: "peanut butter", firstHourRate: BigDecimal.valueOf(firstHour),
                secondHourRate: BigDecimal.valueOf(secondHour),
                afterThirdHourMultiplier: BigDecimal.valueOf(multiplier),
                currency: RatePlan.Currency.PLN)
    }
}
