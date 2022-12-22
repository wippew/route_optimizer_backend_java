package DG.DA;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class DurationService {
	private static String HERE_URL = "https://route.api.here.com/routing/7.2/calculateroute.json";

	public static Integer[][] getDurationMatrix(int totalNodes, List<MaintenanceWorkDTO> data) throws IOException, URISyntaxException {
		Integer[][] array = new Integer[totalNodes][totalNodes];
		for (int i = 0; i < totalNodes; i++) {
			for (int j = 0; j < totalNodes; j++) {
				if (i != j) {
					String waypoint0 = data.get(i).waypoint;
					String waypoint1 = data.get(j).waypoint;
					array[i][j] = sendHttpRequest(waypoint0, waypoint1);
					System.out.println(i);
				} else {
					array[i][j] = 0;
				}
			}
		}
		return array;
	}

	public static Integer sendHttpRequest(String waypoint0, String waypoint1) throws URISyntaxException {
	    HttpGet httpGet = new HttpGet(HERE_URL);
	    URI uri = new URIBuilder(httpGet.getURI())
	      .addParameter("waypoint0", waypoint0)
	      .addParameter("waypoint1", waypoint1)
	      .addParameter("mode", "fastest;car;traffic:disabled")
	      .addParameter("app_id", "CWt4Io2jWFGGLV9csUeX")
	      .addParameter("app_code", "ow1GDLeuAgI2yoDwidUKFw")
	      .build();
	    httpGet.setURI(uri);
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    try {
	    	HttpResponse response = httpClient.execute(httpGet);
	    	String JSONString = EntityUtils.toString(response.getEntity(),
                    "UTF-8");
            JSONObject jsonObject = new JSONObject(JSONString);
            Object baseTime = jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").get("baseTime");
            return (Integer) baseTime;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	    return 0;
	}

}
