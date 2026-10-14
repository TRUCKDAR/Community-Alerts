package escuelaing.edu.co.truckcar.Community_Alerts.dto.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import escuelaing.edu.co.truckcar.community_alerts.dto.event.EventEnvelope;
import escuelaing.edu.co.truckcar.community_alerts.dto.event.RoadAlertEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventEnvelopeTest {

    @Test
    void serializesStandardAlertEnvelope() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        EventEnvelope<RoadAlertEvent> envelope = EventEnvelope.<RoadAlertEvent>builder()
                .eventId(eventId)
                .eventType("alert.reported")
                .occurredAt(Instant.parse("2026-10-10T23:00:00Z"))
                .correlationId(correlationId)
                .version(1)
                .producer("community-alerts")
                .payload(RoadAlertEvent.builder().alertId(42L).eventAction("CREATED").build())
                .build();

        String json = mapper.writeValueAsString(envelope);
        EventEnvelope<RoadAlertEvent> restored = mapper.readValue(
                json,
                mapper.getTypeFactory().constructParametricType(EventEnvelope.class, RoadAlertEvent.class)
        );

        assertThat(restored.getEventId()).isEqualTo(eventId);
        assertThat(restored.getEventType()).isEqualTo("alert.reported");
        assertThat(restored.getOccurredAt()).isEqualTo(Instant.parse("2026-10-10T23:00:00Z"));
        assertThat(restored.getCorrelationId()).isEqualTo(correlationId);
        assertThat(restored.getVersion()).isEqualTo(1);
        assertThat(restored.getProducer()).isEqualTo("community-alerts");
        assertThat(restored.getPayload().getAlertId()).isEqualTo(42L);
    }
}
