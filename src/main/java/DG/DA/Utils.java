package DG.DA;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import static DG.DA.MaintenanceService.getMaintenances;


public class Utils {
	
	public static List<MaintenanceWorkDTO> getDataForTasks(int numberOfTasks, int depot0VehicleCount, int depot1VehicleCount) throws IOException, InterruptedException {
		JSONArray jsonArray = getMaintenances();
		ArrayList<MaintenanceWorkDTO> maintenanceWorkDTOS = populateMaintenancesFromJson(jsonArray, numberOfTasks);
		int depotIndex = 0;
		if (depot0VehicleCount > 0) {
			//depot 0 hardcoded
			Double[] depot0Coordinates = new Double[2];
			depot0Coordinates[0] = 60.20673161432754;
			depot0Coordinates[1] = 24.878853391086857;
			MaintenanceWorkDTO depot0MaintenanceDTO = createDepotDTO(depot0Coordinates, "DEPOT0");
			maintenanceWorkDTOS.add(depotIndex, depot0MaintenanceDTO);
			depotIndex++;
		}

		if (depot1VehicleCount > 0) {
			// depot 1 hardcoded
			Double[] depot1Coordinates = new Double[2];
			depot1Coordinates[0] = 60.213267693282745;
			depot1Coordinates[1] = 25.04715587870096;
			MaintenanceWorkDTO depot1MaintenanceDTO = createDepotDTO(depot1Coordinates, "DEPOT1");
			maintenanceWorkDTOS.add(depotIndex, depot1MaintenanceDTO);
		}
		return maintenanceWorkDTOS;
	}

	public static MaintenanceWorkDTO createDepotDTO(Double[] depotCoordinates, String depotName) {
		String depotWaypoint = depotCoordinates[0].toString() + "," + depotCoordinates[1].toString();
		MaintenanceWorkDTO depotMaintenanceDTO = new MaintenanceWorkDTO(depotCoordinates, 0, depotName, depotWaypoint);
		return depotMaintenanceDTO;
	}

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
	
	public static int convertTypeToDemand(String type) {
		switch(type) {
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
			case "Kävelytarkastus 1 v kevät":
				return TypeConstants.KAVELYTARKASTUS_1V_KEVAT;
			case "Kaapit ja kojut 12kk":
				return TypeConstants.KAAPIT_JA_KOJUT_12KK;
			case "Liikennepaikkatarkastus 1v":
				return TypeConstants.LIIKENNEPAIKKATARKASTUS_1V;
			default:
				return 60*60;
		}
	}
	
	public static List<String> replaceWithArrows(List<String> array) {
		List<String> ret = new ArrayList<>();
		for (String str: array) {
			ret.add(str.replace("_","->" ));
		}
		return ret;
	}
	
	
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
	
}
