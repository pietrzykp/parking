package pl.pietrzyk.parking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pietrzyk.parking.application.dto.SlotState;
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeter;
import pl.pietrzyk.parking.domain.parkingmeter.ParkingMeterRepository;
import pl.pietrzyk.parking.application.exception.ParkingMeterNotFoundException;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
class OperatorFacade {

    private final ParkingMeterRepository parkingMeterRepository;

    SlotState isSlotPaidFor(Long parkingMeterId) {
        ParkingMeter parkingMeter = parkingMeterRepository.findById(parkingMeterId)
                .orElseThrow(ParkingMeterNotFoundException::new);

        return new SlotState(parkingMeter.isTaken());
    }
}
