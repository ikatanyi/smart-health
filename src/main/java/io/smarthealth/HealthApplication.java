package io.smarthealth;

import static java.time.ZoneId.of;
import java.util.TimeZone;
import static java.util.TimeZone.getTimeZone;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
@Slf4j
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
    
    @PostConstruct
    void started() {
        TimeZone.setDefault(getTimeZone(of("Africa/Nairobi")));
    }

}
