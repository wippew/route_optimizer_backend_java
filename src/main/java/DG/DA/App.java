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
	
	public static List<MaintenanceRoute> runLPSolve(int vehicleCount) throws IOException, InterruptedException, URISyntaxException {

		int depotCount = 1;
		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);
		for (int i = 0; i < depotCount; i++) {
			depots.add(new ArrayList<Integer>());
		}


		//depots.get(0).add(0);
		for (int i = 0; i < vehicleCount; i++) {
			depots.get(0).add(i);
		}
//		depots.get(0).add(1);
//		depots.get(1).add(2);
//		depots.get(1).add(3);

//		for (List<Integer> depot : depots) {
//			vehicleCount += depot.size();
//		}
		int taskCount = 4;
		int totalCount = depotCount + taskCount;
		int timeOfWorkingDay = 16 * 3600;

		List<MaintenanceWorkDTO> data = Utils.getDataForTasks(taskCount);

		boolean fetchNewDurations = true;
		if (fetchNewDurations) {
			Integer[][] duration = DurationService.getDurationMatrix(totalCount, data);
			SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
			List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, data, vehicleCount, depots, depotCount);
			List<MaintenanceRoute> list = new ArrayList<>();
			int current = 0;
			for (int i = 0; i < routesAsString.size(); i++) {
				for (int j = 0; j < routesAsString.get(i).size(); j++) {
					// starting from depot
					if (i != 0 && j == 0) {
						MaintenanceRoute maintenanceRoute = new MaintenanceRoute();
						maintenanceRoute.type = data.get(0).type;
						maintenanceRoute.vehicle = String.valueOf(i);
						maintenanceRoute.order = "0";
						maintenanceRoute.coordinates = data.get(0).coordinates;
						list.add(maintenanceRoute);
					} else {
						MaintenanceRoute maintenanceRoute = new MaintenanceRoute();
						maintenanceRoute.type = data.get(current).type;
						maintenanceRoute.vehicle = String.valueOf(i);
						maintenanceRoute.order = String.valueOf(j);
						maintenanceRoute.coordinates = data.get(current).coordinates;
						list.add(maintenanceRoute);
						current++;
					}
				}
			}
			return list;
		} else {
			return new ArrayList<>();
		}

	}
}
