package io.smarthealth;

import io.smarthealth.sequence.SequenceNumberService;
import static java.time.ZoneId.of;
import java.util.TimeZone;
import static java.util.TimeZone.getTimeZone;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
@Slf4j
public class HealthApplication {

//     @Autowired
//    private SequenceNumberService sequenceNumberService;

//    @Bean
//    public Reactor reactor(final Environment env) {
//        Reactor reactor = Reactors.reactor(env, Environment.RING_BUFFER);
//
//        reactor.on($("sequence"), new Consumer<Event<SequenceEvent>>() {
//            @Override
//            public void accept(Event<SequenceEvent> ev) {
//                SequenceEvent data = ev.getData();
//                DeferredResult<String> result = data.getResult();
//                try {
//                    String number = sequenceNumberService.next(data.getTenant(), data.getName());
//                    result.setResult("{ \"number\": \"" + number + "\" }\n");
//                } catch (Exception e) {
//                    log.error("Exception in sequence event listener", e);
//                    result.setErrorResult(e);
//                }
//            }
//        });
//
//        return reactor;
//    }
    
    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
    
    @PostConstruct
    void started() {
        TimeZone.setDefault(getTimeZone(of("Africa/Nairobi")));
    }

}
