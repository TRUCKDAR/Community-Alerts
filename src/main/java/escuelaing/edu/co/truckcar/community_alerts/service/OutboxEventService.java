package escuelaing.edu.co.truckcar.community_alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import escuelaing.edu.co.truckcar.community_alerts.dto.event.EventEnvelope;
import escuelaing.edu.co.truckcar.community_alerts.dto.event.RoadAlertEvent;
import escuelaing.edu.co.truckcar.community_alerts.model.OutboxEvent;
import escuelaing.edu.co.truckcar.community_alerts.model.RoadAlert;
import escuelaing.edu.co.truckcar.community_alerts.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void enqueueAlertEvent(RoadAlert alert, String eventType, String eventAction) {
        RoadAlertEvent payload = RoadAlertEvent.builder()
                .alertId(alert.getId())
                .clientAlertId(alert.getClientAlertId())
                .driverId(alert.getDriverId())
                .alertType(alert.getAlertType().name())
                .severity(alert.getSeverity().name())
                .title(alert.getTitle())
                .description(alert.getDescription())
                .text(freeText(alert))
                .roadCorridor(alert.getRoadCorridor())
                .activeRouteCorridor(alert.getRoadCorridor())
                .latitude(alert.getLatitude())
                .longitude(alert.getLongitude())
                .maxClearanceMeters(alert.getMaxClearanceMeters())
                .confirmationsCount(alert.getConfirmationsCount())
                .eventAction(eventAction)
                .timestamp(LocalDateTime.now())
                .build();

        EventEnvelope<RoadAlertEvent> envelope = EventEnvelope.<RoadAlertEvent>builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .occurredAt(Instant.now())
                .correlationId(correlationId(alert.getClientAlertId()))
                .version(1)
                .producer("community-alerts")
                .payload(payload)
                .build();

        try {
            repository.save(OutboxEvent.builder()
                    .id(envelope.getEventId())
                    .aggregateType("RoadAlert")
                    .aggregateId(alert.getId().toString())
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(envelope))
                    .occurredAt(envelope.getOccurredAt())
                    .createdAt(Instant.now())
                    .attempts(0)
                    .build());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar el evento de alerta para Outbox", exception);
        }
    }

    private String freeText(RoadAlert alert) {
        String title = alert.getTitle() == null ? "" : alert.getTitle().trim();
        String description = alert.getDescription() == null ? "" : alert.getDescription().trim();
        String text = title.isEmpty() || description.isEmpty() ? title + description : title + ". " + description;
        return text.isEmpty() ? null : text;
    }

    private UUID correlationId(String clientAlertId) {
        try {
            return UUID.fromString(clientAlertId);
        } catch (IllegalArgumentException exception) {
            return UUID.nameUUIDFromBytes(clientAlertId.getBytes(StandardCharsets.UTF_8));
        }
    }
}
