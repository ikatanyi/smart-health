package io.smarthealth.inpatient.setup.data;

import io.smarthealth.inpatient.setup.domain.Bed;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BedData {
   private Long id;
    private String name;
    private String description;
    public Bed.Status status;
    private Long roomId;
    private String room;
    private Boolean active;
    private List<BedChargeData> bedCharges = new ArrayList<>();
}
