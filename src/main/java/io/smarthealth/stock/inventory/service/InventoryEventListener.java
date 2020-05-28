package io.smarthealth.stock.inventory.service;

import io.smarthealth.stock.inventory.events.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryItemService service;

    @JmsListener(destination = "stockBalanceQueue", containerFactory = "connectionFactory")
    public void receive(InventoryEvent inventoryEvent) {
            service.processInventoryBalance(inventoryEvent);
            
//        InventoryEvent event = inventoryEvent;
//        
//        switch (event.getType()) {
//            case Increase:
//                service.increase(event.getItem(), event.getStore(), event.getQuantity());
//                break;
//            case Decrease:
//                service.decrease(event.getItem(), event.getStore(), event.getQuantity());
//                break;
//            case Adjustment:
//                service.adjustment(event.getItem(), event.getStore(), event.getQuantity());
//            default:
//                log.info("Nothing to calculate balance");
//        }
    }

}
