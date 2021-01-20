/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.config;

import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Kelsas
 */
@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initProducts(ItemRepository repo, SequenceNumberService sequenceNumberService) {
        return args -> { 
        
        
            Optional<Item> item = repo.findFirstByCategory(ItemCategory.Receipt);
            if (!item.isPresent()) {
                
                String sku = sequenceNumberService.next(1L, Sequences.ServiceItem.name());
                
                Item toSave = new Item();
                toSave.setActive(Boolean.TRUE);
                toSave.setBillable(Boolean.TRUE);
                  toSave.setItemName("Receipt");
                toSave.setCategory(ItemCategory.Receipt);
                toSave.setCostRate(BigDecimal.ZERO);
                toSave.setDrug(Boolean.FALSE);
                toSave.setItemCode(sku);
                toSave.setRate(BigDecimal.ZERO);
                toSave.setItemType(ItemType.Service);

                log.info("save {}", repo.save(toSave));
            }
            
            Optional<Item> copay = repo.findFirstByCategory(ItemCategory.CoPay);
            if (!copay.isPresent()) {
                
                String sku = sequenceNumberService.next(1L, Sequences.ServiceItem.name());
                
                Item toSave = new Item();
                toSave.setActive(Boolean.TRUE);
                toSave.setItemName("Copayment");
                toSave.setBillable(Boolean.TRUE);
                toSave.setCategory(ItemCategory.CoPay);
                toSave.setCostRate(BigDecimal.ZERO);
                toSave.setDrug(Boolean.FALSE);
                toSave.setItemCode(sku);
                toSave.setRate(BigDecimal.ZERO);
                toSave.setItemType(ItemType.Service);

                log.info("save {}", repo.save(toSave));
            }

        };
    }
    //
}
