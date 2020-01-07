package io.smarthealth.stock.inventory.service;

import io.smarthealth.stock.inventory.domain.StockAdjustment;
import io.smarthealth.stock.inventory.domain.StockEntry;
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
    
    private final InventoryBalanceService service;
    
     @JmsListener(destination = "stockBalanceQueue", containerFactory = "connectionFactory")
    public void receive(Object stocks) {
        if(stocks instanceof StockEntry){
            StockEntry entry=(StockEntry) stocks;
            log.info(" >>  Received Stock Entry : " + entry.toString()); 
        }
        if(stocks instanceof  StockAdjustment){
            StockAdjustment entry=(StockAdjustment) stocks;
            log.info(" >>  Received Stock Adjustment : " + entry.toString()); 
        }
         log.info(" >>  Received Stock Balance Request"); 
    }
    
    
}
