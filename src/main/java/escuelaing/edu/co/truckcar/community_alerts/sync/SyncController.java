package escuelaing.edu.co.truckcar.community_alerts.sync;

import escuelaing.edu.co.truckcar.community_alerts.exception.AlertUnauthenticatedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Ingesta por lotes de las alertas viales registradas sin conexión por la PWA del conductor. */
@RestController
@RequestMapping("/api/v1/alerts/sync")
@RequiredArgsConstructor
public class SyncController {

    private final AlertSyncService syncService;

    @PostMapping("/batch")
    public ResponseEntity<SyncBatchResponse> ingest(
            @RequestHeader(value = "X-Auth-User", required = false) String authenticatedUserId,
            @Valid @RequestBody SyncBatchRequest request) {
        if (authenticatedUserId == null || authenticatedUserId.isBlank()) {
            throw new AlertUnauthenticatedException("Se requiere un usuario autenticado para sincronizar alertas");
        }
        return ResponseEntity.ok(syncService.process(authenticatedUserId, request));
    }
}
