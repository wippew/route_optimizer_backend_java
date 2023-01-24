package DG.DA;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.ortools.linearsolver.MPSolver;
import org.json.JSONArray;
import org.json.JSONObject;

import static DG.DA.MaintenanceService.getMaintenances;


public class Utils {

    public static void printRoutesToSystemOutput(int depot1VehicleCount, int depot2VehicleCount, List<List<String>> routesAsString) {
        if (depot1VehicleCount > 0) {

            int current = 0;
            for (int i = 0; i < depot1VehicleCount; i++) {
                // orderCorrectly takes and array and startDepot as arguments
                System.out.println("the array " + i + " is hence: ");
                System.out.println(Utils.orderCorrectly(routesAsString.get(i), "0"));
                current++;
            }

            for (int i = 0; i < depot2VehicleCount; i++) {
                // orderCorrectly takes and array and startDepot as arguments
                System.out.println("the array " + current + " is hence: ");
                System.out.println(Utils.orderCorrectly(routesAsString.get(current), "1"));
                current++;
            }

        } else {
            for (int i = 0; i < depot2VehicleCount; i++) {
                // orderCorrectly takes and array and startDepot as arguments
                System.out.println("the array " + i + " is hence: ");
                System.out.println(Utils.orderCorrectly(routesAsString.get(i), "0"));
            }
        }
    }


    public static List<MaintenanceWorkDTO> getDataForTasks(int numberOfTasks, int depot0VehicleCount, int depot1VehicleCount) throws IOException, InterruptedException {
        JSONArray jsonArray = getMaintenances();
        ArrayList<MaintenanceWorkDTO> maintenanceWorkDTOS = populateMaintenancesFromJson(jsonArray, numberOfTasks);
        int depotIndex = 0;
        if (depot0VehicleCount > 0) {
            Double[] depot0Coordinates = LocationConstants.depot0Coordinates;
            MaintenanceWorkDTO depot0MaintenanceDTO = createDepotDTO(depot0Coordinates, "DEPOT0");
            maintenanceWorkDTOS.add(depotIndex, depot0MaintenanceDTO);
            depotIndex++;
        }
        if (depot1VehicleCount > 0) {
            Double[] depot1Coordinates = LocationConstants.depot1Coordinates;
            MaintenanceWorkDTO depot1MaintenanceDTO = createDepotDTO(depot1Coordinates, "DEPOT1");
            maintenanceWorkDTOS.add(depotIndex, depot1MaintenanceDTO);
        }
        return maintenanceWorkDTOS;
    }

    // method to create a depot MaintenanceWorkDTO
    public static MaintenanceWorkDTO createDepotDTO(Double[] depotCoordinates, String depotName) {
        String depotWaypoint = depotCoordinates[0].toString() + "," + depotCoordinates[1].toString();
        MaintenanceWorkDTO depotMaintenanceDTO = new MaintenanceWorkDTO(depotCoordinates, 0, depotName, depotWaypoint);
        return depotMaintenanceDTO;
    }

    // created MaintenanceWorkDTOs out of received maintenance data
    private static ArrayList<MaintenanceWorkDTO> populateMaintenancesFromJson(JSONArray jsonArray, int numberOfTasks) {
        int nodeCount = numberOfTasks;
        ArrayList<MaintenanceWorkDTO> ret = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String status = obj.getString("extState");
            if ((status.equals("Osoitettu") || status.equals("Avoin") || status.equals("Uusi")) && !obj.isNull("location")) {
                JSONArray jsonCoordinates = obj.getJSONObject("location").getJSONArray("coordinates");
                Double[] coordinates = new Double[2];
                coordinates[0] = (double) jsonCoordinates.get(1);
                coordinates[1] = (double) jsonCoordinates.get(0);
                String type = obj.getJSONObject("workProperties").getJSONObject("laiteryhma").getString("value");
                Integer demand = convertTypeToDemand(type);
                String waypoint = coordinates[0].toString() + "," + coordinates[1].toString();
                MaintenanceWorkDTO maintenanceWorkDTO = new MaintenanceWorkDTO(coordinates, demand, type, waypoint);
                ret.add(maintenanceWorkDTO);
            } else {
                nodeCount++;
            }
        }

        return ret;
    }

    // method that converts a type to a certain estimated maintenance duration
    public static int convertTypeToDemand(String type) {
        switch (type) {
            case "Rumputarkastus 1v":
                return TypeConstants.RUMPUTARKASTUS_1V;
            case "Siltatarkastus 1v":
                return TypeConstants.SILTATARKASTUS_1V;
            case "Vaihde 2v huolto":
                return TypeConstants.VAIHDE_2V_HUOLTO;
            case "Opastinhuolto 12kk":
                return TypeConstants.OPASTINHUOLTO_12KK;
            case "Akselinlaskijahuolto 12 kk":
                return TypeConstants.AKSELINLASKIJAHUOLTO_12KK;
            case "Kaapit ja kojut 12kk":
                return TypeConstants.KAAPIT_JA_KOJUT_12KK;
            default:
                return 60 * 60;
        }
    }

    // replaces the underscores in the optimized routes to arrows for clarity's sake
    public static List<String> replaceWithArrows(List<String> array) {
        List<String> ret = new ArrayList<>();
        for (String str : array) {
            ret.add(str.replace("_", "->"));
        }
        return ret;
    }

    //orders the route to a more easily readable form, starting and ending at depot
    public static List<String> orderCorrectly(List<String> array, String startDepot) {
        List<String> ret = new ArrayList<>();
        String lastEnd = startDepot;
        for (String i : array) {
            List<String> next = getNextInOrder(array, lastEnd);
            lastEnd = next.get(1);
            ret.add(next.get(0));
        }

        ret = replaceWithArrows(ret);
        return ret;
    }

    //used by method above
    public static List<String> getNextInOrder(List<String> array, String lastEnd) {
        List<String> retList = new ArrayList<>();
        for (String i : array) {
            String[] split = i.split("_");
            if (split[0].equals(lastEnd)) {
                lastEnd = split[1];
                retList.add(i);
                retList.add(lastEnd);
                return retList;
            }

        }
        return retList;
    }


    public static List<MaintenanceRoute> populateMaintenanceRoutes(List<List<String>> routesAsString, List<MaintenanceWorkDTO> data) {
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
    }
}
