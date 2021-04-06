package io.smarthealth.clinical.laboratory.data;

import io.smarthealth.clinical.laboratory.domain.LabTestConsumables;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LabTestConsumablesData {
    private String consumableItemName;
    private Long consumableItemId;
    private Long labRegisterId;
    private String labRegisterNumber;
    private int quantity;
    private String unitOfMeasure;

    @NotNull(message = "Please provide the type : Other/Reagent")
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
        return data;
    }

}
