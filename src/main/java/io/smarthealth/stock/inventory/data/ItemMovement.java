package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ItemMovement {
   private String itemCode;
   private String itemName;
   private LocalDate date;
   private String reference;
   private String description;
   private String client;
   private Double quantity;
   private MovementType movementType;
    private MovementPurpose purpose;
    public ItemMovement() {
    }

    public ItemMovement(String itemCode, String itemName, LocalDate date, String reference, MovementType movementType,MovementPurpose purpose, String client, Double quantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.date = date;
        this.reference = reference;
        this.client = client;
        this.quantity = quantity;
        this.movementType = movementType;
        this.purpose = purpose;
        String type = movementType!=null ? movementType.name() : "";
        String pur = purpose!=null ? purpose.name() : "";
        this.description = type.concat(" ").concat(pur);
    }
}
