package escuelaing.edu.co.truckcar.Community_Alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import escuelaing.edu.co.truckcar.community_alerts.model.AlertSeverity;
import escuelaing.edu.co.truckcar.community_alerts.model.AlertType;
import escuelaing.edu.co.truckcar.community_alerts.model.OutboxEvent;
import escuelaing.edu.co.truckcar.community_alerts.model.RoadAlert;
import escuelaing.edu.co.truckcar.community_alerts.repository.OutboxEventRepository;
import escuelaing.edu.co.truckcar.community_alerts.service.OutboxEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceTest {

    @Mock
    private OutboxEventRepository repository;

    @Test
    void enqueuesSerializedReportedAlertEnvelope() {
        OutboxEventService service = new OutboxEventService(repository,
                new ObjectMapper().registerModule(new JavaTimeModule()));
        RoadAlert alert = RoadAlert.builder()
                .id(42L)
                .clientAlertId("client-alert-42")
                .driverId("driver-1")
                .alertType(AlertType.RETEN)
                .severity(AlertSeverity.ALTA)
                .title("Control vial")
                .roadCorridor("Bogota - Cali")
                .latitude(4.7)
                .longitude(-74.1)
                .confirmationsCount(1)
                .build();

        service.enqueueAlertEvent(alert, "alert.reported", "CREATED");

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        OutboxEvent event = captor.getValue();
        assertThat(event.getEventType()).isEqualTo("alert.reported");
        assertThat(event.getAggregateId()).isEqualTo("42");
        assertThat(event.getPublishedAt()).isNull();
        assertThat(event.getPayload()).contains("\"eventType\":\"alert.reported\"");
        assertThat(event.getPayload()).contains("\"alertId\":42");
        assertThat(event.getPayload()).contains("\"correlationId\"");
    }
}
