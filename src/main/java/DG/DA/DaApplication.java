package DG.DA;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static DG.DA.MaintenanceService.getMaintenances;
import static DG.DA.Utils.convertTypeToDemand;

@SpringBootApplication
public class DaApplication {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        SpringApplication.run(DaApplication.class, args);
        List<MaintenanceRoute> routes = App.runLPSolve();

        System.out.print("ASASDA");
    }


}
