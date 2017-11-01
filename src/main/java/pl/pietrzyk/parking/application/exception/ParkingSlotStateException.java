package pl.pietrzyk.parking.application.exception;

public class ParkingSlotStateException extends RuntimeException {

    private ParkingSlotStateException(String message) {
        super(message);
    }

    public static ParkingSlotStateException slotTakenException() {
        return new ParkingSlotStateException("This parking slot is already taken");
    }

    public static ParkingSlotStateException slotEmptyException() {
        return new ParkingSlotStateException("This parking slot is not taken");
    }

    public static ParkingSlotStateException driverNotMatchingSlotException() {
        return new ParkingSlotStateException("This parking slot is not taken by this driver");
    }
}
