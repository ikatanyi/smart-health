package io.smarthealth.clinical.laboratory.data;

import io.smarthealth.clinical.laboratory.domain.LabTestConsumables;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LabTestConsumablesData {
    private String consumableItemName;
    private Long consumableItemId;
    private Long labRegisterId;
    private String labRegisterNumber;
    private Double quantity;
    private String unitOfMeasure;

    @NotNull(message = "Store location is required")
    private Long storeId;

    @ApiModelProperty(hidden=true, required=false) //for swagger documentation purpose
    private String storeName;


    @NotNull(message = "Please provide the type i.e Other/Reagent")
    private String type;//Other/Reagent

    public static LabTestConsumablesData map(LabTestConsumables e) {
        LabTestConsumablesData data = new LabTestConsumablesData();
        data.setConsumableItemName(e.getItem().getItemName());
        data.setConsumableItemId(e.getItem().getId());
        data.setLabRegisterId(e.getLabRegister().getId());
        data.setLabRegisterNumber(e.getLabRegister().getLabNumber());
        data.setQuantity(e.getQuantity());
        data.setUnitOfMeasure(e.getUnitOfMeasure());
        data.setType(e.getType());
        data.setStoreId(e.getStore().getId());
        data.setStoreName(e.getStore().getStoreName());
        return data;
    }

}
