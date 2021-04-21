package io.smarthealth.clinical.laboratory.data;

import io.smarthealth.clinical.laboratory.domain.LabTestReagent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LabTestReagentData {

    private Long testId;
    private String testName;
    private Long equipmentId;
    private String equipmentName;
    private Long reagentServiceId;
    private String reagentServiceName;
    private Double estimatedQuantity;
    private String uom;

    //to display only
    private Double availableQuantity;//in store selected


    public static LabTestReagentData map(LabTestReagent e) {
        LabTestReagentData data = new LabTestReagentData();
        data.setEquipmentId(e.getEquipment().getId());
        data.setEquipmentName(e.getEquipment().getEquipmentName());
        data.setTestId(e.getTest().getId());
        data.setTestName(e.getTest().getTestName());
        data.setReagentServiceId(e.getReagentService().getId());
        data.setReagentServiceName(e.getReagentService().getItemName());
        data.setUom(e.getReagentService().getUnit());
        data.setEstimatedQuantity(e.getEstimatedQuantity());
        return data;
    }

    public static List<LabTestReagentData> map(List<LabTestReagent> es) {
        List<LabTestReagentData> d = new ArrayList<>();
        for (LabTestReagent e : es) {
            LabTestReagentData data = new LabTestReagentData();
            data.setEquipmentId(e.getEquipment().getId());
            data.setEquipmentName(e.getEquipment().getEquipmentName());
            data.setTestId(e.getTest().getId());
            data.setTestName(e.getTest().getTestName());
            data.setReagentServiceId(e.getReagentService().getId());
            data.setReagentServiceName(e.getReagentService().getItemName());
            data.setUom(e.getReagentService().getUnit());
            data.setEstimatedQuantity(e.getEstimatedQuantity());
            d.add(data);
        }
        return d;
    }

}
