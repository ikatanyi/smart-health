/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.events;

import io.smarthealth.stock.inventory.service.InventoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Component
public class InventorySpringEventListener implements ApplicationListener<InventoryCreatedEvent> {

    @Autowired
    private InventoryItemService service;

    @Override
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION )
    public void onApplicationEvent(InventoryCreatedEvent e) {
        service.processInventoryBalance(e);
    }

}
