package io.smarthealth.clinical.triage.data;

import io.smarthealth.clinical.triage.domain.ExtraVitalValue;
import lombok.Data;

@Data
public class ExtraVitalValueData {
    private Long id;
    private Long fieldId;
    private Long vitalRecordId;
    private String fieldName;
    private String value;
    private String fieldLabel;

    public static ExtraVitalValueData map(ExtraVitalValue e) {
        ExtraVitalValueData d = new ExtraVitalValueData();
        d.setValue(e.getValue());
        d.setFieldId(e.getField().getId());
        d.setId(e.getId());
        d.setVitalRecordId(e.getVitalRecord().getId());
        d.setFieldName(e.getField().getName());
        d.setFieldLabel(e.getField().getLabel());
        return d;
    }
}
