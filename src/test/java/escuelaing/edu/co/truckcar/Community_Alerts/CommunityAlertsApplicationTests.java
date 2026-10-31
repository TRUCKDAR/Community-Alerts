package escuelaing.edu.co.truckcar.Community_Alerts;

import escuelaing.edu.co.truckcar.community_alerts.CommunityAlertsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// El paquete de las pruebas (Community_Alerts) no es el de la aplicación (community_alerts): en un sistema de
// archivos que distingue mayúsculas (Linux, CI) Spring no encuentra la configuración buscando hacia arriba.
@SpringBootTest(classes = CommunityAlertsApplication.class)
class CommunityAlertsApplicationTests {

	@Test
	void contextLoads() {
	}

}
