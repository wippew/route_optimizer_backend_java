package DG.DA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public class App {
	
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	public static List<MaintenanceRoute> runLPSolve(int depot1VehicleCount, int depot2VehicleCount) throws IOException, InterruptedException, URISyntaxException {

		int depotCount = 0;
		if (depot1VehicleCount > 0) {
			depotCount++;
		}
		if (depot2VehicleCount > 0) {
			depotCount++;
		}



		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);

		for (int i = 0; i < depotCount; i++) {
			depots.add(new ArrayList<Integer>());
		}
		int totalNumberOfVehicles = depot1VehicleCount + depot2VehicleCount;

		if (depot1VehicleCount > 0) {
			//add vehicles to depot0
			for (int i = 0; i < depot1VehicleCount; i++) {
				depots.get(0).add(i);
			}
			//add vehicles to depot1
			for (int i = depot1VehicleCount; i < totalNumberOfVehicles; i++) {
				depots.get(1).add(i);
			}
		} else {
			for (int i = 0; i < depot2VehicleCount; i++) {
				depots.get(0).add(i);
			}
		}

		int taskCount = 8;
		int totalCount = depotCount + taskCount;

		List<MaintenanceWorkDTO> data = Utils.getDataForTasks(taskCount, depot1VehicleCount, depot2VehicleCount);

		// join tasks that have the same type and coordinates
		for (int i = depotCount; i < data.size(); i++) {
			MaintenanceWorkDTO currentFirstTask = data.get(i);
			for (int j = depotCount; j < data.size(); j++) {
				MaintenanceWorkDTO currentSecondTask = data.get(j);
				if (i != j) {
					if (Arrays.equals(currentFirstTask.coordinates,currentSecondTask.coordinates)) {
						int newDemand = currentFirstTask.demand + currentSecondTask.demand;
						data.get(i).demand = newDemand;
						data.remove(j);
						totalCount--;
					}
				}
			}
		}

		boolean fetchNewDurations = true;
		if (fetchNewDurations) {
			Integer[][] duration = DurationService.getDurationMatrix(totalCount, data);
			SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
			List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, data, depot1VehicleCount, depot2VehicleCount, depots, depotCount);
			List<MaintenanceRoute> list = new ArrayList<>();
			for (int i = 0; i < routesAsString.size(); i++) {
				for (int j = 0; j < routesAsString.get(i).size(); j++) {
					String edgeAsSplit = routesAsString.get(i).get(j);
					String edgeFirstNode = edgeAsSplit.split("_")[0];
					int current = Integer.parseInt(edgeFirstNode);
					MaintenanceRoute maintenanceRoute = new MaintenanceRoute();
					maintenanceRoute.type = data.get(current).type;
					maintenanceRoute.vehicle = String.valueOf(i);
					maintenanceRoute.order = String.valueOf(j);
					maintenanceRoute.coordinates = data.get(current).coordinates;
					list.add(maintenanceRoute);

				}
			}
			return list;
		} else {
			return new ArrayList<>();
		}

	}
}
