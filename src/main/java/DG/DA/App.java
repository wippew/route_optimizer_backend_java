package DG.DA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static List<MaintenanceRoute> runLPSolve(int depot1VehicleCount, int depot2VehicleCount) throws IOException, InterruptedException, URISyntaxException {

        int depotCount = Utils.calculateDepotCount(depot1VehicleCount, depot2VehicleCount, 0);
        List<List<Integer>> depots = Utils.createDepotLists(depotCount);
        Utils.populateDepotsWithVehicles(depot1VehicleCount, depot2VehicleCount, depots);

        int taskCount = ControlConstants.taskCount;
        int totalCount = depotCount + taskCount;

        // Creates DTOs for Tasks
        List<MaintenanceWorkDTO> data = Utils.getDataForTasks(taskCount, depot1VehicleCount, depot2VehicleCount);
        // Join tasks with precisely the same coordinates
        data = Utils.joinTasksWithSameCoordinates(depotCount, totalCount, data);

        // bit to control wheter new driving durations should be fetched at all
        boolean fetchNewDurations = true;
        if (fetchNewDurations) {
            Integer[][] duration = DurationService.getDurationMatrix(totalCount, data);
            SOLVE_LP_ORTOOLS ortools = new SOLVE_LP_ORTOOLS();
            List<List<String>> routesAsString = ortools.SolveOrToolsLP(duration, data, depot1VehicleCount, depot2VehicleCount, depots, depotCount);
            return Utils.populateMaintenanceRoutes(routesAsString, data);
        } else {
            return new ArrayList<>();
        }

    }
}
