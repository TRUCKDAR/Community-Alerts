package escuelaing.edu.co.truckcar.community_alerts.repository;

import escuelaing.edu.co.truckcar.community_alerts.model.RoadAlertConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadAlertConfirmationRepository
        extends JpaRepository<RoadAlertConfirmation, RoadAlertConfirmation.Key> {

    boolean existsByAlertIdAndUserId(Long alertId, String userId);
}
