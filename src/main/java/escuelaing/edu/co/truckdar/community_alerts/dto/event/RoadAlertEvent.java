package escuelaing.edu.co.truckdar.community_alerts.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadAlertEvent {
    private Long alertId;
    private String clientAlertId;
    private String driverId;
    private String alertType;
    private String severity;
    private String title;
    private String roadCorridor;
    private Double latitude;
    private Double longitude;
    private Double maxClearanceMeters;
    private Integer confirmationsCount;
    private String eventAction;
    private LocalDateTime timestamp;
}