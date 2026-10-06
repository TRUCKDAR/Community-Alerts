package escuelaing.edu.co.truckdar.community_alerts.controller;

import escuelaing.edu.co.truckdar.community_alerts.dto.request.CreateRoadAlertRequest;
import escuelaing.edu.co.truckdar.community_alerts.dto.request.OfflineBatchSyncRequest;
import escuelaing.edu.co.truckdar.community_alerts.dto.response.RoadAlertResponse;
import escuelaing.edu.co.truckdar.community_alerts.dto.response.SyncSummaryResponse;
import escuelaing.edu.co.truckdar.community_alerts.service.IRoadAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoadAlertController {

    private final IRoadAlertService service;

    @PostMapping
    public ResponseEntity<RoadAlertResponse> createAlert(@Valid @RequestBody CreateRoadAlertRequest request) {
        return new ResponseEntity<>(service.createAlert(request), HttpStatus.CREATED);
    }

    @PostMapping("/sync-offline")
    public ResponseEntity<SyncSummaryResponse> syncOfflineBatch(@Valid @RequestBody OfflineBatchSyncRequest request) {
        return ResponseEntity.ok(service.syncOfflineBatch(request));
    }

    @PatchMapping("/{alertId}/confirm")
    public ResponseEntity<RoadAlertResponse> confirmAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(service.confirmAlert(alertId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<RoadAlertResponse>> getNearbyAlerts(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "50000") double radiusMeters) {
        return ResponseEntity.ok(service.getNearbyAlerts(latitude, longitude, radiusMeters));
    }

    @GetMapping
    public ResponseEntity<List<RoadAlertResponse>> getAllActive() {
        return ResponseEntity.ok(service.getAllActiveAlerts());
    }
}