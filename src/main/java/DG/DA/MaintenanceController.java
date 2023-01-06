package DG.DA;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

@CrossOrigin
@RestController
public class MaintenanceController {

	@GetMapping("/getRoutes")
	public HashMap<String, List<Map<String, Object>>> getRoutes(@RequestParam("vehicleCount") String vehicleCount) throws Exception {
		Integer numberOfVehicles = Integer.valueOf(vehicleCount);
		List<MaintenanceRoute> routes = App.runLPSolve(numberOfVehicles);
	    HashMap<String, List<Map<String, Object>>> map = new HashMap<>();
	    for (int i = 0; i < routes.size(); i++) {
	    	Map<String, Object> test = new HashMap<>();
	    	String type = routes.get(i).type;
		    String vehicle = routes.get(i).vehicle;		    
		    String order = routes.get(i).order;
		    Double[] coordinates = routes.get(i).coordinates;
		    test.put("type", type);
		    test.put("vehicle", vehicle);
		    test.put("order", order);
		    test.put("coordinates", coordinates);
		    var array = new ArrayList<Map<String, Object>>();
			array.add(test);
		    if (map.containsKey("features")) {
		    	map.get("features").add(test);
			} else {
				map.put("features", array);
			}

	    }
	    return map;
	}

	@GetMapping("/getRoutes2")
	public String getRoutes2() throws Exception {
		return "ASDASDASD";
	}
	
	
}
