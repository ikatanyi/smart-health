package io.smarthealth.notifications.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsynchronousSpringEventsConfig {

//    @Bean(name = "applicationEventMulticaster")
//    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
//        SimpleApplicationEventMulticaster eventMulticaster
//                = new SimpleApplicationEventMulticaster();
//
//        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        return eventMulticaster;
//    }
}
