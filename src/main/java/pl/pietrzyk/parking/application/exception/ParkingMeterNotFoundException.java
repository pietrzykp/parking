package pl.pietrzyk.parking.application.exception;

public class ParkingMeterNotFoundException extends RuntimeException {
    public ParkingMeterNotFoundException() {
        super("No such parking meter");
    }
}
