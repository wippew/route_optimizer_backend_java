package DG.DA;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;


@SpringBootApplication
public class DaApplication {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        SpringApplication.run(DaApplication.class, args);
    }
}
