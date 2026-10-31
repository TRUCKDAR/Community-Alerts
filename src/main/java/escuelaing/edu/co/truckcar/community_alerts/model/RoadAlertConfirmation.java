package escuelaing.edu.co.truckcar.community_alerts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Un voto por usuario y alerta (el reporte original cuenta como el voto de su autor). La clave primaria
 * (alert_id, user_id) hace que dos confirmaciones simultáneas del mismo usuario no puedan sumar dos veces.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RoadAlertConfirmation.Key.class)
@Table(name = "road_alert_confirmations")
public class RoadAlertConfirmation {

    @Id
    @Column(name = "alert_id", nullable = false)
    private Long alertId;

    @Id
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements Serializable {
        private Long alertId;
        private String userId;
    }
}
