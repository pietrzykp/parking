package pl.pietrzyk.parking.application

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import pl.pietrzyk.parking.MockMvcSpec
import pl.pietrzyk.parking.application.dto.ParkingMeterForm
import pl.pietrzyk.parking.domain.driver.Driver
import pl.pietrzyk.parking.domain.driver.DriverRepository
import pl.pietrzyk.parking.domain.history.History
import pl.pietrzyk.parking.domain.history.HistoryRepository
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeter
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeterRepository
import pl.pietrzyk.parking.domain.rateplan.RatePlan
import pl.pietrzyk.parking.domain.rateplan.RatePlanRepository

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

class DriverSpec extends MockMvcSpec {

    @Autowired
    private DriverRepository driverRepository

    @Autowired
    private ParkingMeterRepository parkingMeterRepository

    @Autowired
    private RatePlanRepository ratePlanRepository

    @Autowired
    private HistoryRepository historyRepository

    private RatePlan ratePlan
    private Driver driver


    def setup() {
        historyRepository.deleteAll()
        parkingMeterRepository.deleteAll()
        driverRepository.deleteAll()
        ratePlanRepository.deleteAll()


        def unsavedPlan = new RatePlan(description: "peanut butter", firstHourRate: new BigDecimal(1),
            secondHourRate: new BigDecimal(2), afterThirdHourMultiplier: new BigDecimal(2),
            currency: RatePlan.Currency.PLN)
        ratePlan = ratePlanRepository.save(unsavedPlan)

        def unsavedDriver = new Driver(ratePlan: ratePlan)
        driver = driverRepository.save(unsavedDriver)
    }

    def "should not start parking meter if driver does not exist"() {
        given:
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId() + 100, parkingMeterId: 5L)

        when:
            def response = mockMvc.perform(post('/driver/meter/start')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response

        then:
            response.status == 400
            response.contentAsString == "No such driver"
    }

    def "should not start not existing parking meter"() {
        given:
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId(), parkingMeterId: 311L)

        when:
            def response = mockMvc.perform(post('/driver/meter/start')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response

        then:
            response.status == 400
            response.contentAsString == "No such parking meter"
    }

    def "should not restart taken parking meter"() {
        given:
            def parkingMeter = new ParkingMeter(taken: true, startDateTime: LocalDateTime.MIN,
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId(), parkingMeterId: parkingMeter.getId())

        when:
            def response = mockMvc.perform(post('/driver/meter/start')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response

        then:
            response.status == 400
            response.contentAsString == "This parking slot is already taken"
    }

    def "should start parking meter"() {
        given:
            def parkingMeter = new ParkingMeter(taken: false, startDateTime: LocalDateTime.MIN,
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId(), parkingMeterId: parkingMeter.getId())

        when:
            def response = mockMvc.perform(post('/driver/meter/start')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response

            def savedParkingMeter = parkingMeterRepository.findById(parkingMeter.getId())

        then:
            response.status == 200
            savedParkingMeter.isPresent()
            savedParkingMeter.get().isTaken()
            savedParkingMeter.get().getStartDateTime() != LocalDateTime.MIN
    }

    def "should stop parking meter"() {
        given:
            def parkingMeter = new ParkingMeter(driver: driver, taken: true, startDateTime:
                    LocalDateTime.now().minusHours(5),
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId(), parkingMeterId: parkingMeter.getId())

        when:
            def response = mockMvc.perform(post('/driver/meter/stop')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response

            def savedParkingMeter = parkingMeterRepository.findById(parkingMeter.getId())
            def savedHistory = historyRepository.findAll()

            def content = new JsonSlurper().parseText(response.contentAsString)

        then:
            response.status == 200
            savedParkingMeter.get().getEndDateTime() != LocalDateTime.MIN
            content.payment == new BigDecimal(63)
            savedHistory.size() == 1
    }

    def "should not stop not taken parking meter"() {
        given:
            def parkingMeter = new ParkingMeter(taken: false, startDateTime: LocalDateTime.MIN,
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)
            def parkingMeterForm = new ParkingMeterForm(driverId: driver.getId(), parkingMeterId: parkingMeter.getId())

        when:
            def response = mockMvc.perform(post('/driver/meter/stop')
                    .content(toJson(parkingMeterForm)).contentType(MediaType.APPLICATION_JSON))
                    .andReturn().response
        then:
            response.status == 400
            response.contentAsString == "This parking slot is not taken"
    }

    def "should return proper payment for driver"() {
        given:
            def secondDriver = new Driver(ratePlan: ratePlan)
            secondDriver = driverRepository.save(secondDriver)

            def firstParking = new History(driver: driver, startDateTime: LocalDateTime.MIN,
                endDateTime: LocalDateTime.MIN, paymentAmount: new BigDecimal(100), currency: RatePlan.Currency.PLN)
            def secondParking = new History(driver: driver, startDateTime: LocalDateTime.MIN,
                endDateTime: LocalDateTime.MIN, paymentAmount: new BigDecimal(45.5), currency: RatePlan.Currency.PLN)
            def secondDriverParking = new History(driver: secondDriver, startDateTime: LocalDateTime.MIN,
                endDateTime: LocalDateTime.MIN, paymentAmount: new BigDecimal(12.5), currency: RatePlan.Currency.PLN)

            historyRepository.save(new ArrayList<History>(
                    Arrays.asList(firstParking, secondParking, secondDriverParking)))

        when:
            def response = mockMvc.perform(get('/driver/summary')
                    .param('driverId', driver.getId().toString()))
                .andReturn().response

            def content = new JsonSlurper().parseText(response.contentAsString)

        then:
            response.status == 200
            content.size == 1
            content.get(0).sum == new BigDecimal(145.5)
    }
}