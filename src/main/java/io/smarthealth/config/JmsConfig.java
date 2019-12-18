package io.smarthealth.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.jms.Queue;

/**
 *
 * @author Kelsas
 */
@Configuration
public class JmsConfig {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("journal-queue");
    }
}
