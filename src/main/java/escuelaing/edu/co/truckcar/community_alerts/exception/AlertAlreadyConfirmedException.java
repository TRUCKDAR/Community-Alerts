package escuelaing.edu.co.truckcar.community_alerts.exception;

/** El usuario ya confirmó (o reportó) esta alerta: cada usuario cuenta una sola vez (409). */
public class AlertAlreadyConfirmedException extends RuntimeException {

    public AlertAlreadyConfirmedException(String message) {
        super(message);
    }
}
