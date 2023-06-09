package DG.DA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static List<MaintenanceRoute> runLPSolve(int depot1VehicleCount, int depot2VehicleCount) throws IOException, InterruptedException, URISyntaxException {

        int depotCount = calculateDepotCount(depot1VehicleCount, depot2VehicleCount, 0);
        List<List<Integer>> depots = createDepotLists(depotCount);
        populateDepotsWithVehicles(depot1VehicleCount, depot2VehicleCount, depots);

        // how many tasks for the algorithm to choose from
        int taskCount = ControlConstants.taskCount;

        // Creates DTOs for Tasks
        List<MaintenanceWorkDTO> data = Utils.getDataForTasks(taskCount, depot1VehicleCount, depot2VehicleCount);
        // Join tasks with precisely the same coordinates
        data = joinTasksWithSameCoordinates(depotCount, depotCount + taskCount, data);

        // total number of nodes
        int totalCount = data.size();

        // bit to control wheter new driving durations should be fetched at all
        boolean fetchNewDurations = true;
        if (fetchNewDurations) {
            Integer[][] duration = DurationService.getDurationMatrix(totalCount, data);
            SolveWithORTools ortools = new SolveWithORTools();
            List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, data, depot1VehicleCount, depot2VehicleCount, depots, depotCount);
            return Utils.populateMaintenanceRoutes(routesAsString, data);
        } else {
            return new ArrayList<>();
        }

    }

    // calculates the real depotCount that takes empty depots to account
    public static int calculateDepotCount(int depot1VehicleCount, int depot2VehicleCount, int depotCount) {
        if (depot1VehicleCount > 0) {
            depotCount++;
        }
        if (depot2VehicleCount > 0) {
            depotCount++;
        }
        return depotCount;
    }

    // initializes depot list
    public static List<List<Integer>> createDepotLists(int depotCount) {
        List<List<Integer>> depots = new ArrayList<List<Integer>>(depotCount);

        for (int i = 0; i < depotCount; i++) {
            depots.add(new ArrayList<Integer>());
        }
        return depots;
    }
    // adds the correct vehicles to the correct depots
    public static void populateDepotsWithVehicles(int depot1VehicleCount, int depot2VehicleCount, List<List<Integer>> depots) {
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
    }

    public static List<MaintenanceWorkDTO> joinTasksWithSameCoordinates(int depotCount, int totalCount, List<MaintenanceWorkDTO> data) {
        // join tasks that have the same coordinates
        for (int i = depotCount; i < totalCount; i++) {
            MaintenanceWorkDTO currentFirstTask = data.get(i);
            for (int j = i; j < totalCount; j++) {
                if (i != j) {
                    MaintenanceWorkDTO currentSecondTask = data.get(j);
                    if (Arrays.equals(currentFirstTask.coordinates, currentSecondTask.coordinates)) {
                        int newDemand = currentFirstTask.demand + currentSecondTask.demand;
                        currentFirstTask.demand = newDemand;
                        data.remove(currentSecondTask);
                        totalCount--;
                        j = i;
                    }
                }
            }
            //joinedData.add(currentFirstTask);
        }
        return data;
    }
}
