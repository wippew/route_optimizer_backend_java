package DG.DA;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class MaintenanceController {

	@GetMapping("/getRoutes")
	public HashMap<String, List<Map<String, Object>>> getRoutes(@RequestParam("depot1VehicleCount") String depot1VehicleCount,
																@RequestParam("depot2VehicleCount") String depot2VehicleCount) throws Exception {
		Integer depot1VehicleCountAsNumber = Integer.valueOf(depot1VehicleCount);
		Integer depot2VehicleCountAsNumber = Integer.valueOf(depot2VehicleCount);
		List<MaintenanceRoute> routes = App.runLPSolve(depot1VehicleCountAsNumber, depot2VehicleCountAsNumber);
	    HashMap<String, List<Map<String, Object>>> map = new HashMap<>();
	    for (int i = 0; i < routes.size(); i++) {
	    	Map<String, Object> task = new HashMap<>();
			task.put("type", routes.get(i).type);
			task.put("vehicle", routes.get(i).vehicle);
			task.put("order", routes.get(i).order);
			task.put("coordinates", routes.get(i).coordinates);
		    if (map.containsKey("features")) {
		    	map.get("features").add(task);
			} else {
				ArrayList<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
				array.add(task);
				map.put("features", array);
			}
	    }
	    return map;
	}
	
	
}
