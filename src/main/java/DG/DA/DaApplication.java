package DG.DA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DaApplication.class, args);
		App.runLPSolve();
	}
}
