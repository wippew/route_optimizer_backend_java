package DG.DA;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static DG.DA.MaintenanceService.getMaintenances;
import static DG.DA.Utils.getDataForTasks;

@SpringBootApplication
public class DaApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(DaApplication.class, args);
		JSONArray jsonObject = getMaintenances();
		System.out.print("ASASDA");
	}
}
