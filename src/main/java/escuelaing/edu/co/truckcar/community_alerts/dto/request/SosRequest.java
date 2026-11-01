package escuelaing.edu.co.truckcar.community_alerts.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Pedido de auxilio del conductor. El conductor es siempre el del JWT (X-Auth-User). clientEventId (opcional) hace
 * idempotente el reenvío: el mismo SOS repetido no crea otro.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SosRequest {

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud fuera de rango")
    @DecimalMax(value = "90.0", message = "Latitud fuera de rango")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud fuera de rango")
    @DecimalMax(value = "180.0", message = "Longitud fuera de rango")
    private Double longitude;

    @PositiveOrZero(message = "La precisión no puede ser negativa")
    private Double accuracyMeters;

    private Long loadId;

    @Size(max = 20, message = "La placa admite hasta 20 caracteres")
    private String vehiclePlate;

    @Size(max = 150, message = "El nombre admite hasta 150 caracteres")
    private String driverName;

    @Size(max = 500, message = "El mensaje admite hasta 500 caracteres")
    private String message;

    private UUID clientEventId;
}
