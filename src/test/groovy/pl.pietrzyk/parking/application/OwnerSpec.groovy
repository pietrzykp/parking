package pl.pietrzyk.parking.application

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import pl.pietrzyk.parking.MockMvcSpec
import pl.pietrzyk.parking.domain.driver.Driver
import pl.pietrzyk.parking.domain.driver.DriverRepository
import pl.pietrzyk.parking.domain.history.History
import pl.pietrzyk.parking.domain.history.HistoryRepository
import pl.pietrzyk.parking.domain.rateplan.RatePlan
import pl.pietrzyk.parking.domain.rateplan.RatePlanRepository

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class OwnerSpec extends MockMvcSpec {

    @Autowired
    private HistoryRepository historyRepository

    @Autowired
    private DriverRepository driverRepository

    @Autowired
    private RatePlanRepository ratePlanRepository

    private Driver driver


    def setup() {
        historyRepository.deleteAll()
        driverRepository.deleteAll()
        ratePlanRepository.deleteAll()

        def ratePlan = new RatePlan(description: "peanut butter", firstHourRate: new BigDecimal(1),
                secondHourRate: new BigDecimal(2), afterThirdHourMultiplier: new BigDecimal(2),
                currency: RatePlan.Currency.PLN)
        ratePlan = ratePlanRepository.save(ratePlan)

        def unsavedDriver = new Driver(ratePlan: ratePlan)
        driver = driverRepository.save(unsavedDriver)
    }

    def "should sum entries for specific day only"() {
        given:
            def historyEntry = new History(driver: driver, startDateTime: LocalDateTime.MIN,
                endDateTime:  LocalDateTime.of(2001, 4, 21, 12, 12), paymentAmount: new BigDecimal(100),
                    currency: RatePlan.Currency.PLN)

            def historyEntry2 = new History(driver: driver, startDateTime: LocalDateTime.MIN,
                endDateTime:  LocalDateTime.of(2001, 4, 21, 00, 00), paymentAmount: new BigDecimal(50),
                currency: RatePlan.Currency.PLN)

            def historyEntry3 = new History(driver: driver, startDateTime: LocalDateTime.MIN,
                endDateTime:  LocalDateTime.of(2001, 4, 22, 12, 12), paymentAmount: new BigDecimal(925),
                currency: RatePlan.Currency.PLN)

            historyRepository.save(new ArrayList(Arrays.asList(historyEntry, historyEntry2, historyEntry3)))

        when:

            def response = mockMvc.perform(get('/owner/day-summary')
                .param('date', "2001-04-21"))
                .andReturn().response


            def content = new JsonSlurper().parseText(response.contentAsString)

        then:
            response.status == 200
            content.get(0).sum == new BigDecimal(150)
            content.get(0).currency == "PLN"
    }

    def "should return empty list for no entries"() {
        when:
            def response = mockMvc.perform(get('/owner/day-summary')
                    .param('date', "2001-04-21"))
                    .andReturn().response

            def content = new JsonSlurper().parseText(response.contentAsString)

        then:
            response.status == 200
            content.isEmpty()
    }
}
