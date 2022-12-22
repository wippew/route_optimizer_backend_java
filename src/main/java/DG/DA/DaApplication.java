package DG.DA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static DG.DA.MaintenanceService.getMaintenances;

@SpringBootApplication
public class DaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DaApplication.class, args);
		getMaintenances();
	}
}
