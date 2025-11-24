package fatec.vortek.cimob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CimobApplication {

    public static void main(String[] args) {
        SpringApplication.run(CimobApplication.class, args);
    }
}
