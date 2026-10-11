package escuelaing.edu.co.truckdar.Community_Alerts.service;

import escuelaing.edu.co.truckdar.community_alerts.dto.request.CreateRoadAlertRequest;
import escuelaing.edu.co.truckdar.community_alerts.model.AlertSeverity;
import escuelaing.edu.co.truckdar.community_alerts.model.AlertType;
import escuelaing.edu.co.truckdar.community_alerts.model.RoadAlert;
import escuelaing.edu.co.truckdar.community_alerts.repository.RoadAlertRepository;
import escuelaing.edu.co.truckdar.community_alerts.service.OutboxEventService;
import escuelaing.edu.co.truckdar.community_alerts.service.RoadAlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadAlertDeduplicationTest {

    @Mock
    private RoadAlertRepository repository;

    @Mock
    private OutboxEventService outboxEventService;

    @Test
    void associatesRecentNearbyAlertAndEmitsValidatedWhenThresholdIsReached() {
        RoadAlert existing = RoadAlert.builder()
                .id(10L)
                .clientAlertId("existing-alert")
                .alertType(AlertType.DERRUMBE)
                .confirmationsCount(2)
                .isActive(true)
                .build();
        when(repository.findRecentDuplicate(any(), any(Double.class), any(Double.class), any(), any(Double.class)))
                .thenReturn(Optional.of(existing));
        when(repository.save(any(RoadAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoadAlertService service = new RoadAlertService(repository, new GeometryFactory(), outboxEventService);
        CreateRoadAlertRequest request = CreateRoadAlertRequest.builder()
                .clientAlertId("new-alert")
                .driverId("driver-1")
                .alertType(AlertType.DERRUMBE)
                .severity(AlertSeverity.ALTA)
                .title("Derrumbe")
                .latitude(4.7)
                .longitude(-74.1)
                .build();

        assertThat(service.createAlert(request).getId()).isEqualTo(10L);

        assertThat(existing.getConfirmationsCount()).isEqualTo(3);
        verify(outboxEventService).enqueueAlertEvent(existing, "alert.confirmed", "CONFIRMED");
        verify(outboxEventService).enqueueAlertEvent(existing, "alert.validated", "VALIDATED");
        verify(repository).save(existing);
    }
}
