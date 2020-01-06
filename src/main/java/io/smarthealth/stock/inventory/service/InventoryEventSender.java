package io.smarthealth.stock.inventory.service;

import io.smarthealth.stock.inventory.events.InventoryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class InventoryEventSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void process(InventoryEvent event) {
        jmsTemplate.convertAndSend("stockBalanceQueue", event);
    }
}
