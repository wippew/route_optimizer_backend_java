package DG.DA;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MaintenanceController {

	@GetMapping("/employees")
	public Map<String, String> sayHello() {
	    HashMap<String, String> map = new HashMap<>();
	    map.put("key", "value");
	    map.put("foo", "bar");
	    map.put("aa", "bb");
	    return map;
	}
	
	@GetMapping("/employees2")
	public String helloWorld()
	 {	     // Print statement
	     return "Hello World!";
	 }
	
	
}
