package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.BedType;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class BedTypeData {
    @ApiModelProperty(hidden=true)
    private Long id;
    private String name;
    private String description;
    private Boolean active = Boolean.TRUE;
    @ApiModelProperty(hidden=true)
    List<ChargeData>charges;
    

    public BedType map() {
        BedType d = new BedType();
        d.setDescription(this.getDescription());
        d.setName(this.getName());
        d.setIsActive(this.getActive());
        return d;
    }
}
