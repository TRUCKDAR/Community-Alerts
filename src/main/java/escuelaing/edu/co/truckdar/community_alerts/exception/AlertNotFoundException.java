package escuelaing.edu.co.truckdar.community_alerts.exception;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) {
        super(message);
    }
}