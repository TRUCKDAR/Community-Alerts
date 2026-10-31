package escuelaing.edu.co.truckcar.community_alerts.dto.request;

import jakarta.validation.Valid;
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

    /** Lo fija RoadAlertController con el X-Auth-User del gateway; el valor que envíe el cliente se descarta. */
    private String driverId;

    @NotEmpty(message = "El lote de alertas no puede estar vacío")
    @Valid
    private List<CreateRoadAlertRequest> alerts;
}