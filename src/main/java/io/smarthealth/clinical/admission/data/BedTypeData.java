package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.BedType;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BedTypeData {

    private String name;
    private String description;

    public static BedTypeData map(BedType type) {
        BedTypeData d = new BedTypeData();
        d.setDescription(type.getDescription());
        d.setName(type.getName());
        return d;
    }
}
