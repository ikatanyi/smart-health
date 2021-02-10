package io.smarthealth.clinical.triage.data;

import io.smarthealth.clinical.triage.domain.ExtraVitalField;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class ExtraVitalFieldData {

    private String name;
    private String label;
    @Enumerated(EnumType.STRING)
    private ExtraVitalFieldsEnums type;
    private Boolean required = Boolean.FALSE;

    public static ExtraVitalFieldData map(ExtraVitalField e) {
        ExtraVitalFieldData d = new ExtraVitalFieldData();
        d.setLabel(e.getLabel());
        d.setType(e.getType());
        d.setName(e.getName());
        d.setRequired(e.getRequired());
        return d;
    }

    public static ExtraVitalField map(ExtraVitalFieldData d) {
        ExtraVitalField e = new ExtraVitalField();
        e.setLabel(d.getLabel());
        e.setName(d.getName());
        e.setType(d.getType());
        e.setRequired(d.getRequired());
        return e;
    }
}
