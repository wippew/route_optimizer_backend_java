package DG.DA;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class MaintenanceService {

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public static JSONArray getMaintenances() throws IOException {
        var featureTypes = new String[1];
        featureTypes[0] = "wfs.huolto";
        var propertyRestrictions = new ArrayList<>();
        var orderMap = new HashMap<String, String>() {{
            put("sortOrder", "DESC");
            put("propertyName", "createdDate");
        }};

        JSONObject json = new JSONObject();
        json.put("someKey", "someValue");
        json.put("order", orderMap);
        json.put("assignments", JSONObject.NULL);
        json.put("useBbox", false);
        json.put("bbox", JSONObject.NULL);
        json.put("plannedStartDateEnd", JSONObject.NULL);
        json.put("plannedStartDateStart", JSONObject.NULL);
        json.put("hasWorkRecords", JSONObject.NULL);
        json.put("priorities", JSONObject.NULL);
        json.put("systemStates", JSONObject.NULL);
        json.put("states", JSONObject.NULL);
        json.put("propertyRestrictions", propertyRestrictions);
        json.put("deadlineEnd", "2022-06-15");
        json.put("deadlineStart", "2022-06-08");
        json.put("createdDateEnd", JSONObject.NULL);
        json.put("createdDateStart", JSONObject.NULL);
        json.put("freeText", "");
        json.put("featureTypes", featureTypes);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost request = new HttpPost("http://ned.geometrix.fi/to/REST/v1/tasks/filter");
        StringEntity params = new StringEntity(json.toString());
        request.addHeader("content-type", "application/json");
        request.addHeader("Authorization", getBasicAuthenticationHeader("grkrail.testi", "testi1"));
        request.setEntity(params);
        CloseableHttpResponse response = httpClient.execute(request);
        String JSONString = EntityUtils.toString(response.getEntity(),
                "UTF-8");
        JSONArray jsonArray = new JSONArray(JSONString);
        return jsonArray;
    }
}

