package io.smarthealth.stock.item.service;

import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.domain.TaxRepository;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.ReorderRule;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.UomRepository;
import io.smarthealth.stock.item.domain.specification.ItemSpecification;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class ItemService {
 
    private final TaxRepository taxRepository;
     private final ItemRepository itemRepository; 
     private final UomRepository uomRepository; 
     private final ModelMapper modelMapper;

    public ItemService(TaxRepository taxRepository, ItemRepository itemRepository, UomRepository uomRepository, ModelMapper modelMapper) {
        this.taxRepository = taxRepository;
        this.itemRepository = itemRepository;
        this.uomRepository = uomRepository;
        this.modelMapper = modelMapper;
    }
      
    @Transactional
    public ItemData createItem(CreateItem createItem) {
       Item item=new Item();
       item.setActive(Boolean.TRUE);
       item.setCategory(createItem.getCategory());
       item.setCostRate(createItem.getPurchaseRate());
       item.setDescription(createItem.getDescription());
       item.setItemCode(createItem.getSku());
       item.setItemName(createItem.getItemName());
       item.setItemType(createItem.getItemType());
       item.setRate(createItem.getRate());
       //check the category
       if(createItem.getItemUnit()!=null){
           Optional<Uom> uom = uomRepository.findById(Long.valueOf(createItem.getItemUnit()));
           if(uom.isPresent()){
                item.setUom(uom.get());
           }
       }
        if(createItem.getTaxId()!=null){
           Optional<Tax> tax=  taxRepository.findById(createItem.getTaxId());
           if(tax.isPresent()){
                item.setTax(tax.get());
           }
       }  
         if(createItem.getCategory().equals("Drugs")){
                //then we create a drug item here
                //define the items that belong to drugs
               
            }
          if(createItem.getCategory().equals("Procedure")){
                //then we create a drug item here
                //define the items that belong to drugs
                
            }
        if(createItem.getItemType().equals("Inventory")){
           
            if(createItem.getInitialStock()>0){
                ReorderRule rule=new ReorderRule();
//       item.setReorderRules(reorderRules);
            }
        }
        Item savedItem= itemRepository.save(item);
        return ItemData.map(savedItem);
    }

    public Optional<Item> findById(final Long itemId) {
        return itemRepository.findById(itemId);
    }

    public Optional<Item> findByItemCode(final String itemCode) {
        return itemRepository.findByItemCode(itemCode);
    }
  public Page<Item> fetchItems(final boolean includeClosed, String term,Pageable pageable) { 
        Specification<Item> spec = ItemSpecification.createSpecification(includeClosed, term);
        Page<Item> items = itemRepository.findAll(spec, pageable);
        return items;
    } 
}
