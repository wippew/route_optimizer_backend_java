package DG.DA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class App {
	
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	public static List<MaintenanceRoute> runLPSolve() throws Exception {
		
		int depotCount = 2;
		List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);
		for(int i = 0; i < depotCount; i++)  {
			depots.add(new ArrayList<Integer>());
	    }
		depots.get(0).add(0);
		depots.get(0).add(1);
		depots.get(1).add(2);
		depots.get(1).add(3);
		
		int vehicleCount = 0;
		for (List<Integer> depot: depots) {
			vehicleCount += depot.size();
		}
		int taskCount = 40;
		int totalCount = depotCount + taskCount;
		int timeOfWorkingDay = 6 * 3600;
		
		//List<MaintenanceWorkDTO> data = Utils.getDataForTasks(totalCount);
		
//		int fetchNewDurations = 1;
//		if (fetchNewDurations == 1) {
//			Double[][] duration = DurationService.getDurationMatrix(totalCount, data);
//			SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
//			int[] demand = new int[duration.length];
//			for (double d: demand) {
//				d = 60;
//			}
//			ortools.SolveOrToolsLP(duration, demand);
//		} else {
//			
//		}
		
		Double[][] duration = new Double[totalCount][totalCount];
		for (int i = 0; i < totalCount; i++) {
			for (int j = 0; j < totalCount; j++) {
				if (i == j) {
					duration[i][j] = 0.0;
				} else {
					duration[i][j] = (10.0 + i * j);
				}
			}
		}
		
		int[] demand = new int[totalCount];
		for (int i = 0; i < totalCount; i++) {
			demand[i] = 2400;
		}		
		demand[0] = 0;
		demand[1] = 0;
		
		String[] types = new String[totalCount];
		for (int i = 0; i < totalCount; i++) {
			types[i] = "TESTTYPE";
		}		
		demand[0] = 0;
		demand[1] = 0;
		
		SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
		List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, demand, types, vehicleCount, depots, depotCount);
		List<MaintenanceRoute> list = new ArrayList<>();
		for (int i = 0; i < routesAsString.size(); i++) {
			for (int j = 0; j < routesAsString.get(i).size(); j++) {
				MaintenanceRoute maintenanceRoute = new MaintenanceRoute();
				maintenanceRoute.type = types[i];
				maintenanceRoute.vehicle = String.valueOf(i);
				maintenanceRoute.order = String.valueOf(j);
				maintenanceRoute.xCoordinates = 22.30008653511428;
				maintenanceRoute.yCoordinates = 60.43000065672325;
				list.add(maintenanceRoute);
			}			
		}
		return list;
		
	}
}
