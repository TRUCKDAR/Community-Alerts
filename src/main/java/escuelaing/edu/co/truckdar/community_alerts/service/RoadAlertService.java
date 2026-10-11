package escuelaing.edu.co.truckdar.community_alerts.service;

import escuelaing.edu.co.truckdar.community_alerts.dto.request.CreateRoadAlertRequest;
import escuelaing.edu.co.truckdar.community_alerts.dto.request.OfflineBatchSyncRequest;
import escuelaing.edu.co.truckdar.community_alerts.dto.response.RoadAlertResponse;
import escuelaing.edu.co.truckdar.community_alerts.dto.response.SyncSummaryResponse;
import escuelaing.edu.co.truckdar.community_alerts.exception.AlertNotFoundException;
import escuelaing.edu.co.truckdar.community_alerts.model.RoadAlert;
import escuelaing.edu.co.truckdar.community_alerts.model.AlertType;
import escuelaing.edu.co.truckdar.community_alerts.repository.RoadAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadAlertService implements IRoadAlertService {

    private final RoadAlertRepository repository;
    private final GeometryFactory geometryFactory;
    private final OutboxEventService outboxEventService;

    @org.springframework.beans.factory.annotation.Value("${truckdar.alerts.dedup.radius-meters:500}")
    private double dedupRadiusMeters;

    @org.springframework.beans.factory.annotation.Value("${truckdar.alerts.dedup.window-minutes:30}")
    private long dedupWindowMinutes;

    @org.springframework.beans.factory.annotation.Value("${truckdar.alerts.validation-threshold:3}")
    private int validationThreshold;

    @org.springframework.beans.factory.annotation.Value("${truckdar.alerts.ttl.default-hours:24}")
    private long defaultTtlHours;

    @org.springframework.beans.factory.annotation.Value("${truckdar.alerts.ttl.derrumbe-hours:6}")
    private long derrumbeTtlHours;

    @Override
    @Transactional
    public RoadAlertResponse createAlert(CreateRoadAlertRequest request) {
        String clientAlertId = request.getClientAlertId() != null ? request.getClientAlertId() : UUID.randomUUID().toString();

        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reportedAt = request.getReportedAt() != null ? request.getReportedAt() : now;

        RoadAlert duplicate = repository.findRecentDuplicate(
                request.getAlertType().name(),
                request.getLatitude(),
                request.getLongitude(),
                reportedAt.minusMinutes(dedupWindowMinutes),
                dedupRadiusMeters
        ).orElse(null);
        if (duplicate != null) {
            duplicate.setConfirmationsCount(duplicate.getConfirmationsCount() + 1);
            duplicate.setSyncedAt(now);
            RoadAlert confirmed = repository.save(duplicate);
            outboxEventService.enqueueAlertEvent(confirmed, "alert.confirmed", "CONFIRMED");
            if (confirmed.getConfirmationsCount() >= validationThreshold) {
                outboxEventService.enqueueAlertEvent(confirmed, "alert.validated", "VALIDATED");
            }
            log.info("Alerta duplicada asociada a #{}; confianza={}", confirmed.getId(),
                    confirmed.getConfirmationsCount());
            return mapToResponse(confirmed);
        }

        RoadAlert alert = RoadAlert.builder()
                .clientAlertId(clientAlertId)
                .driverId(request.getDriverId())
                .driverName(request.getDriverName())
                .alertType(request.getAlertType())
                .severity(request.getSeverity())
                .title(request.getTitle())
                .description(request.getDescription())
                .roadCorridor(request.getRoadCorridor())
                .maxClearanceMeters(request.getMaxClearanceMeters())
                .location(point)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .confirmationsCount(1)
                .isActive(true)
                .reportedAt(reportedAt)
                .syncedAt(now)
                .build();

        RoadAlert saved = repository.save(alert);
        outboxEventService.enqueueAlertEvent(saved, "alert.reported", "CREATED");
        log.info("Alerta vial creada [{}] en corredor: {}", saved.getAlertType(), saved.getRoadCorridor());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public RoadAlertResponse confirmAlert(Long alertId) {
        RoadAlert alert = repository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alerta no encontrada con ID: " + alertId));

        alert.setConfirmationsCount(alert.getConfirmationsCount() + 1);
        RoadAlert updated = repository.save(alert);

        outboxEventService.enqueueAlertEvent(updated, "alert.confirmed", "CONFIRMED");
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadAlertResponse> getNearbyAlerts(double latitude, double longitude, double radiusMeters) {
        return repository.findActiveAlertsWithinRadius(latitude, longitude, radiusMeters)
                .stream()
                .filter(this::isActiveWithinTtl)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadAlertResponse> getAllActiveAlerts() {
        return repository.findByIsActiveTrueOrderByReportedAtDesc()
                .stream()
                .filter(this::isActiveWithinTtl)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SyncSummaryResponse syncOfflineBatch(OfflineBatchSyncRequest batchRequest) {
        int inserted = 0;
        int duplicates = 0;
        List<RoadAlertResponse> processed = new ArrayList<>();

        for (CreateRoadAlertRequest req : batchRequest.getAlerts()) {
            if (req.getClientAlertId() != null && repository.findByClientAlertId(req.getClientAlertId()).isPresent()) {
                duplicates++;
                continue;
            }
            RoadAlertResponse res = createAlert(req);
            processed.add(res);
            inserted++;
        }

        log.info("Sincronización offline completada para conductor {}: {} recibidas, {} insertadas, {} duplicadas",
                batchRequest.getDriverId(), batchRequest.getAlerts().size(), inserted, duplicates);

        return SyncSummaryResponse.builder()
                .totalReceived(batchRequest.getAlerts().size())
                .totalInserted(inserted)
                .totalIgnoredDuplicates(duplicates)
                .syncedAlerts(processed)
                .build();
    }

    private RoadAlertResponse mapToResponse(RoadAlert entity) {
        return RoadAlertResponse.builder()
                .id(entity.getId())
                .clientAlertId(entity.getClientAlertId())
                .driverId(entity.getDriverId())
                .driverName(entity.getDriverName())
                .alertType(entity.getAlertType())
                .severity(entity.getSeverity())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .roadCorridor(entity.getRoadCorridor())
                .maxClearanceMeters(entity.getMaxClearanceMeters())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .confirmationsCount(entity.getConfirmationsCount())
                .isActive(entity.getIsActive())
                .reportedAt(entity.getReportedAt())
                .syncedAt(entity.getSyncedAt())
                .build();
    }

    private boolean isActiveWithinTtl(RoadAlert alert) {
        LocalDateTime expiresAt = alert.getReportedAt().plusHours(ttlHours(alert.getAlertType()));
        return expiresAt.isAfter(LocalDateTime.now());
    }

    private long ttlHours(AlertType alertType) {
        return switch (alertType) {
            case DERRUMBE -> derrumbeTtlHours;
            default -> defaultTtlHours;
        };
    }
}