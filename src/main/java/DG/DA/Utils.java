package DG.DA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import static DG.DA.MaintenanceService.getMaintenances;


public class Utils {
	
	public static List<MaintenanceWorkDTO> getDataForTasks(int numberOfTasks) throws IOException, InterruptedException {
		Double[] depotCoordinates = new Double[2];
		depotCoordinates[0] = 60.437520385889826;
		depotCoordinates[1] = 22.338532504743725;
		String depotWaypoint = depotCoordinates[0].toString() + "," + depotCoordinates[1].toString();
		MaintenanceWorkDTO depotMaintenanceDTO = new MaintenanceWorkDTO(depotCoordinates, 0, "DEPOT0", depotWaypoint);
		JSONArray jsonArray = getMaintenances();
		ArrayList<MaintenanceWorkDTO> maintenanceWorkDTOS = populateMaintenancesFromJson(jsonArray, numberOfTasks);
		maintenanceWorkDTOS.add(0, depotMaintenanceDTO);
		return maintenanceWorkDTOS;
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
