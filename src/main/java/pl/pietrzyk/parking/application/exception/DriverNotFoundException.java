package pl.pietrzyk.parking.application.exception;


public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException() {
        super("No such driver");
    }
}
