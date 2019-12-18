package io.smarthealth.config;

import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class JmsMessageService {
    @Autowired
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;
    
     public void publish(Object message){
          jmsTemplate.convertAndSend(queue, message);
     }
     
}
