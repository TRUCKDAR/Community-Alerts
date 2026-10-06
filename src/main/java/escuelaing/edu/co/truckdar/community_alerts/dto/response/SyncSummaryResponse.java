package escuelaing.edu.co.truckdar.community_alerts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncSummaryResponse {
    private int totalReceived;
    private int totalInserted;
    private int totalIgnoredDuplicates;
    private List<RoadAlertResponse> syncedAlerts;
}