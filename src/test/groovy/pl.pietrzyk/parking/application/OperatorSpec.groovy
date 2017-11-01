package pl.pietrzyk.parking.application

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeter
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeterRepository

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class OperatorSpec extends MockMvcSpec {

    @Autowired
    private ParkingMeterRepository parkingMeterRepository

    def setup() {
        parkingMeterRepository.deleteAll()
    }

    def "should return error if parking meter does not exist"() {
        when:
            def response = mockMvc.perform(get('/operator/is-slot-paid-for')
                .param('parkingMeterId', "100"))
                .andReturn().response

        then:
            response.status == 400
            response.contentAsString == "No such parking meter"
    }

    def "should return true if slot is being paid for"() {
        given:
            def parkingMeter = new ParkingMeter(taken: true, startDateTime: LocalDateTime.MIN,
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)

        when:
            def response = mockMvc.perform(get('/operator/is-slot-paid-for')
                    .param('parkingMeterId', parkingMeter.getId().toString()))
                    .andReturn().response

            def content = new JsonSlurper().parseText(response.contentAsString)
        then:
            response.status == 200
            content.isTaken

        cleanup:
            parkingMeterRepository.deleteAll()
    }

    def "should return false if slot is not being paid for"() {
        given:
            def parkingMeter = new ParkingMeter(taken: false, startDateTime: LocalDateTime.MIN,
                    endDateTime: LocalDateTime.MIN)
            parkingMeter = parkingMeterRepository.save(parkingMeter)

        when:
            def response = mockMvc.perform(get('/operator/is-slot-paid-for')
                .param('parkingMeterId', parkingMeter.getId().toString()))
                .andReturn().response

            def content = new JsonSlurper().parseText(response.contentAsString)
        then:
            response.status == 200
            !content.isTaken

        cleanup:
            parkingMeterRepository.deleteAll()
    }
}
