package escuelaing.edu.co.truckcar.community_alerts.exception;

/** Reportar o confirmar alertas exige el X-Auth-User que propaga el api-gateway (401). */
public class AlertUnauthenticatedException extends RuntimeException {

    public AlertUnauthenticatedException(String message) {
        super(message);
    }
}
