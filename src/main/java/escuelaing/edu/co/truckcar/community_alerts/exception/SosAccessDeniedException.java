package escuelaing.edu.co.truckcar.community_alerts.exception;

/** El rol del usuario no permite la operación SOS (pedir ayuda: CONDUCTOR; atender: COORDINADOR o ADMIN). */
public class SosAccessDeniedException extends RuntimeException {
    public SosAccessDeniedException(String message) {
        super(message);
    }
}
