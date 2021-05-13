/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.config;

import io.smarthealth.administration.config.domain.GlobalConfigNum;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.sequence.SequenceDefinition;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Configuration
@Slf4j
public class LoadDatabase {


    @Bean
    CommandLineRunner initProducts(ItemRepository repo, SequenceNumberService sequenceNumberService, ConfigService configService) {
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

            sequenceNumberService.create(1L, Sequences.PurchaseInvoiceNumber.name(), "SIV%07d",1L);
            sequenceNumberService.create(1L, Sequences.StockReturnNumber.name(), "CN%05d",1L);

            if(!configService.findByName(GlobalConfigNum.ShowStockBalancePurchaseOrder.name()).isPresent()){
                GlobalConfiguration conf = new GlobalConfiguration();
                conf.setDescription("Display Current Stock Balances on Purchase Order Printout");
                conf.setEnabled(true);
                conf.setFieldType("Boolean");
                conf.setGroup("System");
                conf.setName(GlobalConfigNum.ShowStockBalancePurchaseOrder.name());
                conf.setValue("0");

                configService.createConfigs(conf);
            }
            if(!configService.findByName(GlobalConfigNum.ShowInvoiceDate.name()).isPresent()){
                GlobalConfiguration conf = new GlobalConfiguration();
                conf.setDescription("Enabling dates on Inpatient Invoices");
                conf.setEnabled(true);
                conf.setFieldType("Boolean");
                conf.setGroup("System");
                conf.setName(GlobalConfigNum.ShowInvoiceDate.name());
                conf.setValue("1");

                configService.createConfigs(conf);
            }

        };
    }
    //
}
