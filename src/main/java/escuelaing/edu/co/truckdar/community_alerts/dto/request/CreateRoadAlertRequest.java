package escuelaing.edu.co.truckdar.community_alerts.dto.request;

import escuelaing.edu.co.truckdar.community_alerts.model.AlertSeverity;
import escuelaing.edu.co.truckdar.community_alerts.model.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoadAlertRequest {

    private String clientAlertId;

    @NotBlank(message = "El ID del conductor es obligatorio")
    private String driverId;

    private String driverName;

    @NotNull(message = "El tipo de alerta es obligatorio")
    private AlertType alertType;

    @NotNull(message = "La severidad es obligatoria")
    private AlertSeverity severity;

    @NotBlank(message = "El título de la alerta es obligatorio")
    private String title;

    private String description;
    private String roadCorridor;
    private Double maxClearanceMeters;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitude;

    private LocalDateTime reportedAt;
}