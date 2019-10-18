package io.smarthealth.stock.item.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateItem {

    private String itemType;
    private String itemName;
    private String sku;
    private double rate;
    private String itemUnit;
    private String description;
    private String category;

    private double purchaseRate; //cost rate
    private String purchaseDescription;
    private Long taxId; //tax

    private Long accountId;
    private Long purchaseAccountId;
    private Long inventoryAccountId;

    private Integer initialStock;
    private double initialStockRate;
    private Integer reorderLevel;
    
//    "item_type":"Inventory",
//   "item_name":"kelvin",
//   "sku":"sc3233",
//   "item_unit":"tablet",
//   "selling_price":"15",
//   "sales_descriptions":"344",
//   "sales_tax":"tax",
//   "cost_price":"10",
//   "cost_descriptions":"purchse descr",
//   "stock_category":"drug",
//   "drug_category":"allergenics",
//   "drug_strength":"f",
//   "drug_route":"injectable",
//   "dose_form":"capsule",
//   "inventory_store":"ms",
//   "stock_balance":"0",
//   "reorder_level":"100",
//   "stock_rate_per_unit":"0",
//   "order_quantity":"1000"
}
