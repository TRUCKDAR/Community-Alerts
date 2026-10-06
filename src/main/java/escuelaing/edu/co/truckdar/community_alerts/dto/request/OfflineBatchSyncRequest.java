package escuelaing.edu.co.truckdar.community_alerts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineBatchSyncRequest {

    @NotBlank(message = "El ID del conductor es obligatorio")
    private String driverId;

    @NotEmpty(message = "El lote de alertas no puede estar vacío")
    @Valid
    private List<CreateRoadAlertRequest> alerts;
}