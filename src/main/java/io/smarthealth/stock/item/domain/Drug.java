//package io.smarthealth.stock.item.domain;
//
//import io.smarthealth.infrastructure.domain.Identifiable;
//import javax.persistence.*;
//import lombok.Data;
//import lombok.ToString;
//
///**
// *
// * @author Kelsas
// */
//
//@Data
//@Entity
//@Table(name = "stock_drugs")
//public class Drug extends Identifiable{
//    @ToString.Exclude
//    @OneToOne
//    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_drugs_item_id"))
//    private Item item;
//    
//    private String drugCategory;
//    private String strength;
//    private String route;
//    private String drugForm;
//    
//}
