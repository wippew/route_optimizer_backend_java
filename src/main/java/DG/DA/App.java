package DG.DA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class App {
	
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	public static List<MaintenanceRoute> runLPSolve() throws IOException, InterruptedException, URISyntaxException {

		int depotCount = 1;
		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);
		for (int i = 0; i < depotCount; i++) {
			depots.add(new ArrayList<Integer>());
		}


		depots.get(0).add(0);
//		for (int i = 0; i < vehicleCount; i++) {
//			depots.get(0).add(i);
//		}
//		depots.get(0).add(1);
//		depots.get(1).add(2);
//		depots.get(1).add(3);

		int vehicleCount = 0;
		for (List<Integer> depot : depots) {
			vehicleCount += depot.size();
		}
		int taskCount = 3;
		int totalCount = depotCount + taskCount;
		int timeOfWorkingDay = 6 * 3600;

		List<MaintenanceWorkDTO> data = Utils.getDataForTasks(totalCount);

		boolean fetchNewDurations = true;
		if (fetchNewDurations) {
			Integer[][] duration = DurationService.getDurationMatrix(totalCount, data);
			SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
			List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, data, vehicleCount, depots, depotCount);
			List<MaintenanceRoute> list = new ArrayList<>();
			for (int i = 0; i < routesAsString.size(); i++) {
				for (int j = 0; j < routesAsString.get(i).size(); j++) {
					MaintenanceRoute maintenanceRoute = new MaintenanceRoute();
					maintenanceRoute.type = data.get(i).type;
					maintenanceRoute.vehicle = String.valueOf(i);
					maintenanceRoute.order = String.valueOf(j);
					maintenanceRoute.coordinates = data.get(i).coordinates;
					list.add(maintenanceRoute);
				}
			}
			return list;
		} else {
			return new ArrayList<>();
		}

	}
}
