package DG.DA;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

@CrossOrigin
@RestController
public class MaintenanceController {

	@GetMapping("/getRoutes")
	public Map<String, Map<String, Object>> getRoutes() throws Exception {
		List<MaintenanceRoute> routes = App.runLPSolve();
	    HashMap<String, Map<String, Object>> map = new HashMap<>();
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
		    map.put("features", test);
	    }
	    return map;
	}

	@GetMapping("/getRoutes2")
	public String getRoutes2() throws Exception {
		return "ASDASDASD";
	}
	
	
}
