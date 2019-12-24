package io.smarthealth.stock.item.data;

import io.smarthealth.infrastructure.utility.AppHelper;
import io.smarthealth.stock.item.domain.Item;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemData {

    private Long itemId;
    private String itemType;
    private String itemName;
    private String itemCategory;
    private String sku;
    private Long uomId;
    private String unit;
    private String description;
    private double rate; 
    private String rateFormatted;
    private String brand;
    private String manufacturer;
    private Long taxId;
    private String taxName;
    private double taxPercentage;
    private String accountName;
    private Long purchaseAccountId;
    private String purchaseAccountName;
    private String purchaseDescription;
    private double purchaseRate; //1000
    private String purchaseRateFormatted; // KES 1,000
    private String ean;
    private double stockOnHand;
    private double availableStock;
    private double actualAvailableStock;
    private Integer reorderLevel;
    private String status;
    private String createdTime;
    private String lastModifiedTime;

    public static ItemData map(Item item){
        ModelMapper modelMapper=new ModelMapper();
        ItemData itemsdata = modelMapper.map(item, ItemData.class); 
        itemsdata.setItemId(item.getId());
        itemsdata.setSku(item.getItemCode());
        itemsdata.setItemCategory(item.getCategory());
        if(item.getUom()!=null){
            itemsdata.setUnit(item.getUom().getName());
            itemsdata.setUomId(item.getId());
        }
        itemsdata.setRateFormatted(AppHelper.toFormattedDecimal(item.getRate())); 
        
        if(item.getTax()!=null){
            itemsdata.setTaxId(item.getTax().getId());
            itemsdata.setTaxName(item.getTax().getTaxName());
            itemsdata.setTaxPercentage(item.getTax().getRate());
        }
        itemsdata.setStatus(item.getActive() ? "Active" : "Inactive");
         
        return itemsdata;
    }
//    
//    public static List<ItemData> map(){
//        
//    }
      
}
