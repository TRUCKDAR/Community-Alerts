package escuelaing.edu.co.truckcar.community_alerts.exception;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) {
        super(message);
    }
}