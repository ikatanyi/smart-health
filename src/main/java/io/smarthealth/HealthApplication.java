package io.smarthealth;

import static java.time.ZoneId.of;
import java.util.TimeZone;
import static java.util.TimeZone.getTimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication 
public class HealthApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }

    //what exactly changed here
    @PostConstruct
    void started() {
        TimeZone.setDefault(getTimeZone(of("Africa/Nairobi")));
    }

}
