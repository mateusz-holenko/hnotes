package houen.hnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class HNotesApplication {
  public static void main(String[] args) {
    SpringApplication.run(HNotesApplication.class, args);
	}
}
